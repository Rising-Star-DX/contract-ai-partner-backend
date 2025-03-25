package com.partner.contract.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class GptMessageRequestDto {
    private String role;
    private String content;

    @Builder
    public GptMessageRequestDto(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
