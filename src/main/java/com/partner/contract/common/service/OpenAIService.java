package com.partner.contract.common.service;

import com.partner.contract.common.dto.GptRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final WebClient openAIConfig;

    @Value("${secret.openAI.version}")
    private String model;

    @Async("gptAsyncExecutor")
    public CompletableFuture<String> callGpt(List<String> messages) {
        GptRequestDto gptRequestDto = GptRequestDto.builder()
                .messages(messages)
                .model(model)
                .top_p(1.0)
                .temperature(0.5)
                .build();

        System.out.println("gptRequestDto = " + gptRequestDto);

        return openAIConfig.post()
                .uri("/v1/chat/completions")
                .bodyValue(gptRequestDto)
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();  // 비동기 처리
    }
}
