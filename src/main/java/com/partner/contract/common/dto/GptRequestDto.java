package com.partner.contract.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class GptRequestDto {
    private String model;
    private List<GptMessageRequestDto> messages;
    private Double temperature;
    private Double top_p;

    @Builder
    public GptRequestDto(String model, List<GptMessageRequestDto> messages, Double temperature, Double top_p) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.top_p = top_p;
    }
}
