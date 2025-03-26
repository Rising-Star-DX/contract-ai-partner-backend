package com.partner.contract.agreement.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.domain.AgreementIncorrectText;
import com.partner.contract.agreement.dto.AgreementAnalysisFlaskResponseDto;
import com.partner.contract.agreement.dto.AgreementIncorrectTextDto;
import com.partner.contract.agreement.repository.AgreementIncorrectTextRepository;
import com.partner.contract.agreement.repository.AgreementRepository;
import com.partner.contract.common.dto.AnalysisRequestDto;
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgreementAnalysisAsyncService {

    private final AgreementRepository agreementRepository;
    private final AgreementIncorrectTextRepository agreementIncorrectTextRepository;
    private final RestTemplate restTemplate;

    @Value("${secret.flask.ip}")
    private String FLASK_SERVER_IP;

    @Async
    @Transactional
    public void analyze(Agreement agreement, String categoryName){
        // Flask에 AI 분석 요청
        String url = FLASK_SERVER_IP + "/flask/agreements/analysis";

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

        FlaskResponseDto<AgreementAnalysisFlaskResponseDto> body;
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
            throw new ApplicationException(ErrorCode.FLASK_SERVER_CONNECTION_ERROR, e.getMessage());
        }

        System.out.println("body = " + body.getData());

        // Flask에서 넘어온 계약서 정보 data
        AgreementAnalysisFlaskResponseDto flaskResponseDto = body.getData();
        List<AgreementIncorrectTextDto> agreementIncorrectTextDtos = flaskResponseDto.getAgreementIncorrectTextDtos();

        // DTO -> Entity 변환 후
        List<AgreementIncorrectText> incorrectTextEntities = agreementIncorrectTextDtos.stream()
                .map(dto -> AgreementIncorrectText.builder()
                        .position(dto.getPosition().toString())
                        .page(dto.getPage())
                        .accuracy(dto.getAccuracy())
                        .incorrectText(dto.getIncorrectText())
                        .proofText(dto.getProofText())
                        .correctedText(dto.getCorrectedText())
                        .agreement(agreement)
                        .build())
                .collect(Collectors.toList());

        agreementIncorrectTextRepository.saveAll(incorrectTextEntities);

        // AI 상태 및 분석 정보 업데이트
        agreement.updateAiStatus(AiStatus.SUCCESS);
        agreement.updateAnalysisInfomation(flaskResponseDto.getTotalPage(), flaskResponseDto.getSummaryContent());

        agreementRepository.save(agreement);
    }
}
