package com.partner.contract.common.dto;

import lombok.Builder;

import java.util.List;

public class GptRequestDto {
    private String model;
    private List<String> messages;
    private Double temperature;
    private Double top_p;

    @Builder
    public GptRequestDto(String model, List<String> messages, Double temperature, Double top_p) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.top_p = top_p;
    }
}
