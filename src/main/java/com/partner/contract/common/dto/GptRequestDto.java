package com.partner.contract.common.dto;

import lombok.Builder;

public class GptRequestDto {
    private String model;
    private String prompt;
    public Double temperature;
    public Double top_p;

    @Builder
    public GptRequestDto(String model, String prompt, Double temperature, Double top_p) {
        this.model = model;
        this.prompt = prompt;
        this.temperature = temperature;
        this.top_p = top_p;
    }
}
