package com.partner.contract.common.service;


import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.domain.AgreementIncorrectPosition;
import com.partner.contract.agreement.domain.AgreementIncorrectText;
import com.partner.contract.agreement.dto.AgreementAnalysisFlaskResponseDto;
import com.partner.contract.agreement.dto.AgreementIncorrectDto;
import com.partner.contract.agreement.dto.IncorrectClauseDataDto;
import com.partner.contract.agreement.repository.AgreementIncorrectPositionRepository;
import com.partner.contract.agreement.repository.AgreementIncorrectTextRepository;
import com.partner.contract.agreement.repository.AgreementRepository;
import com.partner.contract.common.dto.AnalysisRequestDto;
import com.partner.contract.common.dto.FlaskStandardContentsResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.domain.StandardContent;
import com.partner.contract.standard.repository.StandardContentRepository;
import com.partner.contract.standard.repository.StandardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final StandardRepository standardRepository;
    private final StandardContentRepository standardContentRepository;
    private final AgreementRepository agreementRepository;
    private final AgreementIncorrectTextRepository agreementIncorrectTextRepository;
    private final AgreementIncorrectPositionRepository agreementIncorrectPositionRepository;

    @KafkaListener(topics = "${kafka.topics.standard-analysis-response}")
    @Transactional
    public void listenStandardAnalysisResponse(FlaskStandardContentsResponseDto flaskResponseDto) {
        Long standardId = flaskResponseDto.getStandardId();
        Standard standard = standardRepository.findById(standardId).orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        try {
            if ("success".equals(flaskResponseDto.getResult())) { // 기준문서 분석 성공
                standard.updateAiStatus(AiStatus.SUCCESS);
                standard.updateTotalPage(flaskResponseDto.getContents().size());
                standardRepository.save(standard);

                Boolean exists = standardContentRepository.existsByStandardId(standard.getId());

                if(!exists) {
                    List<String> contents = flaskResponseDto.getContents();
                    for (int i = 0; i < contents.size(); i++) {
                        StandardContent standardContent = StandardContent.builder()
                                .page(i + 1)
                                .content(contents.get(i))
                                .standard(standard)
                                .build();

                        standardContentRepository.save(standardContent);
                    }
                }
            } else {
                standard.updateAiStatus(AiStatus.FAILED);
                standardRepository.save(standard);
                log.error("Flask에서 AI 분석에 실패했습니다.");
            }
        } catch (NullPointerException e) {
            standard.updateAiStatus(AiStatus.FAILED);
            standardRepository.save(standard);
            log.error("Flask에서 응답한 data가 null입니다. : {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.agreement-analysis-response}")
    @Transactional(noRollbackFor = ApplicationException.class)
    public void listenAgreementAnalysisResponse(AgreementAnalysisFlaskResponseDto flaskResponseDto) {
        Long agreementId = flaskResponseDto.getAgreementId();
        Agreement agreement = agreementRepository.findById(agreementId).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));

        try {
            if("success".equals(flaskResponseDto.getResult())) {
                // Flask에서 넘어온 계약서 정보 data
                List<AgreementIncorrectDto> agreementIncorrectDtos = flaskResponseDto.getAgreementIncorrectDtos();

                for (AgreementIncorrectDto agreementIncorrectDto : agreementIncorrectDtos) {
                    AgreementIncorrectText agreementIncorrectText = AgreementIncorrectText.builder()
                            .accuracy(agreementIncorrectDto.getAccuracy() * 100)
                            .incorrectText(agreementIncorrectDto.getIncorrectText())
                            .proofText(agreementIncorrectDto.getProofText())
                            .correctedText(agreementIncorrectDto.getCorrectedText())
                            .agreement(agreement)
                            .build();

                    agreementIncorrectTextRepository.save(agreementIncorrectText);

                    for (IncorrectClauseDataDto incorrectClauseDataDto : agreementIncorrectDto.getIncorrectClauseDataDtoList()) {
                        if (incorrectClauseDataDto.getPosition() == null || incorrectClauseDataDto.getPosition().isEmpty() || incorrectClauseDataDto.getPositionPart() == null) {
                            agreement.updateAiStatus(AiStatus.FAILED);
                            agreementRepository.save(agreement);
                            throw new ApplicationException(ErrorCode.AI_ANALYSIS_POSITION_EMPTY_ERROR);
                        }

                        AgreementIncorrectPosition agreementIncorrectPosition = AgreementIncorrectPosition.builder()
                                .position(incorrectClauseDataDto.getPosition().toString())
                                .positionPart(incorrectClauseDataDto.getPositionPart().toString())
                                .page(incorrectClauseDataDto.getPage())
                                .orderIndex(incorrectClauseDataDto.getOrderIndex())
                                .agreementIncorrectText(agreementIncorrectText)
                                .build();

                        agreementIncorrectPositionRepository.save(agreementIncorrectPosition);
                    }
                }

                // AI 상태 및 분석 정보 업데이트
                agreement.updateAiStatus(AiStatus.SUCCESS);
                agreement.updateAnalysisInfomation(flaskResponseDto.getTotalPage(), flaskResponseDto.getTotalChunks());
                agreementRepository.save(agreement);
            } else {
                agreement.updateAiStatus(AiStatus.FAILED);
                agreementRepository.save(agreement);
                throw new ApplicationException(ErrorCode.FLASK_ANALYSIS_ERROR);
            }

        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            agreement.updateAiStatus(AiStatus.FAILED);
            agreementRepository.save(agreement);
            throw new ApplicationException(ErrorCode.FLASK_ANALYSIS_ERROR);
        }
    }
}
