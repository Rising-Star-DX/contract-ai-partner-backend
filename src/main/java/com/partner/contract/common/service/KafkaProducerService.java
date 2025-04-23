package com.partner.contract.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.standard-analysis-request}")
    private String standardAnalysisRequestTopic;
    @Value("${kafka.topics.agreement-analysis-request}")
    private String agreementAnalysisRequestTopic;

    public <T> void sendStandardAnalysisRequest(T message) {
        kafkaTemplate.send(standardAnalysisRequestTopic, message);
    }

    public <T> void sendAgreementAnalysisRequest(T message) {
        kafkaTemplate.send(agreementAnalysisRequestTopic, message);
    }
}
