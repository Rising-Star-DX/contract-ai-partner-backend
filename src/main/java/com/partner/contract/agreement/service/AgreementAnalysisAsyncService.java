package com.partner.contract.agreement.service;

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
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgreementAnalysisAsyncService {

    private final AgreementRepository agreementRepository;
    private final AgreementIncorrectTextRepository agreementIncorrectTextRepository;
    private final AgreementIncorrectPositionRepository agreementIncorrectPositionRepository;
    private final RestTemplate restTemplate;

    @Value("${secret.flask.ip}")
    private String FLASK_SERVER_IP;

    @Async
    @Transactional(noRollbackFor = ApplicationException.class)
    public void analyze(Agreement agreement, String categoryName){
        try {
            // Flask에 AI 분석 요청
            String url = "http://rising-star-alb-885642517.ap-northeast-2.elb.amazonaws.com:5000/flask/agreements/analysis";

            AnalysisRequestDto analysisRequestDto = AnalysisRequestDto.builder()
                    .id(agreement.getId())
                    .url(agreement.getUrl())
                    .categoryName(categoryName)
                    .type(agreement.getType())
                    .build();

            // HTTP Request Header 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP Request Body 설정
            HttpEntity<AnalysisRequestDto> requestEntity = new HttpEntity<>(analysisRequestDto, headers);

            FlaskResponseDto<AgreementAnalysisFlaskResponseDto> body = null;
            try {
                // Flask에 API 요청
                ResponseEntity<FlaskResponseDto<AgreementAnalysisFlaskResponseDto>> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<FlaskResponseDto<AgreementAnalysisFlaskResponseDto>>() {} // ✅ 제네릭 타입 유지
                );

                body = response.getBody();

            } catch (RestClientException e) {
                agreement.updateAiStatus(AiStatus.FAILED);
                agreementRepository.save(agreement);
                throw new ApplicationException(ErrorCode.FLASK_SERVER_CONNECTION_ERROR);
            }

            // Flask에서 넘어온 계약서 정보 data
            AgreementAnalysisFlaskResponseDto flaskResponseDto = body.getData();
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

                for(IncorrectClauseDataDto incorrectClauseDataDto : agreementIncorrectDto.getIncorrectClauseDataDtoList()) {
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

        } catch (ApplicationException e) {
            throw e;
        } catch (Exception e) {
            agreement.updateAiStatus(AiStatus.FAILED);
            agreementRepository.save(agreement);
            throw new ApplicationException(ErrorCode.FLASK_ANALYSIS_ERROR);
        }
    }
}
