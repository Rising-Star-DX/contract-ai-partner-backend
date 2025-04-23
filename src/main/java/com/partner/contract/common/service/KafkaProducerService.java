package com.partner.contract.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "analysis_topic";

    public <T> void sendMessage(T message) {
        kafkaTemplate.send(TOPIC, message);
    }
}
