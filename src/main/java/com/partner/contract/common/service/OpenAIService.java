package com.partner.contract.common.service;

import com.partner.contract.common.dto.GptRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final WebClient openAIConfig;

    @Value("${secret.openAI.version}")
    private String model;

    @Async("gptAsyncExecutor")
    public CompletableFuture<String> callGpt(String prompt) {
        GptRequestDto gptRequestDto = GptRequestDto.builder()
                .prompt(prompt)
                .model(model)
                .top_p(1.0)
                .temperature(0.5)
                .build();

        return openAIConfig.post()
                .uri("/v1/chat/completions")
                .bodyValue(gptRequestDto)
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();  // 비동기 처리
    }
}
