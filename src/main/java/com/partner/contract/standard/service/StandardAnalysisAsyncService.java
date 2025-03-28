package com.partner.contract.standard.service;

import com.partner.contract.common.dto.AnalysisRequestDto;
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.repository.StandardRepository;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class StandardAnalysisAsyncService {

    private final StandardRepository standardRepository;
    private final RestTemplate restTemplate;

    @Value("${secret.flask.ip}")
    private String FLASK_SERVER_IP;

    @Async
    @Transactional
    public void analyze(Standard standard, String categoryName){
        // Flask에 AI 분석 요청
        String url = FLASK_SERVER_IP + "/flask/standards/analysis";

        AnalysisRequestDto analysisRequestDto = AnalysisRequestDto.builder()
                .id(standard.getId())
                .url(standard.getUrl())
                .categoryName(categoryName)
                .type(standard.getType())
                .build();

        // HTTP Request Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP Request Body 설정
        HttpEntity<AnalysisRequestDto> requestEntity = new HttpEntity<>(analysisRequestDto, headers);

        FlaskResponseDto<String> body = null;
        try {
            // Flask에 API 요청
            ResponseEntity<FlaskResponseDto<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<FlaskResponseDto<String>>() {} // ✅ 제네릭 타입 유지
            );

            body = response.getBody();

        } catch (RestClientException e) {
            standard.updateAiStatus(AiStatus.FAILED);
            standardRepository.save(standard);
            log.error("Flask 서버 연결 오류 : {}", e.getMessage(), e);
        }

        try {
            if ("success".equals(body.getData())) { // 기준문서 분석 성공
                standard.updateAiStatus(AiStatus.SUCCESS);
                standardRepository.save(standard);
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
}
