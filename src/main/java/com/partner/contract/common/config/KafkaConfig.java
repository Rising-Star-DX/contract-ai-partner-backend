package com.partner.contract.common.config;

import com.partner.contract.agreement.dto.AgreementAnalysisFlaskResponseDto;
import com.partner.contract.common.dto.FlaskStandardContentsResponseDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${secret.kafka.ip}")
    private String KAFKA_SERVER_IP;

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_IP);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return config;
    }

    @Bean
    public <T> ProducerFactory<String, T> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public <T> KafkaTemplate<String, T> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public Map<String, Object> consumerCommonConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_IP);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "flask-ai-response-group");
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        return config;
    }

    @Bean
    public <T> ConsumerFactory<String, T> standardConsumerFactory() {
        Map<String, Object> config = consumerCommonConfig();
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, FlaskStandardContentsResponseDto.class);
        return new DefaultKafkaConsumerFactory<>(consumerCommonConfig());
    }

    @Bean
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> standardKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(standardConsumerFactory());
        return factory;
    }

    @Bean
    public <T> ConsumerFactory<String, T> agreementConsumerFactory() {
        Map<String, Object> config = consumerCommonConfig();
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AgreementAnalysisFlaskResponseDto.class);
        return new DefaultKafkaConsumerFactory<>(consumerCommonConfig());
    }

    @Bean
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> agreementKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(agreementConsumerFactory());
        return factory;
    }
}
