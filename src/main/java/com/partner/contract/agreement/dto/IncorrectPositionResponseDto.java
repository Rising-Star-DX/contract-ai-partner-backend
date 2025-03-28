package com.partner.contract.agreement.dto;

import lombok.Data;

@Data
public class IncorrectPositionResponseDto {
    private Double left;
    private Double top;
    private Double width;
    private Double height;

    public IncorrectPositionResponseDto(Double left, Double top, Double width, Double height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }
}
