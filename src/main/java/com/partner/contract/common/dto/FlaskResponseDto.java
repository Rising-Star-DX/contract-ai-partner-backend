package com.partner.contract.common.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlaskResponseDto {
    private String code;
    private String message;
    private String data;
}
