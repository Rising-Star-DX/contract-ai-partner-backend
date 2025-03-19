package com.partner.contract.common.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlaskResponseDto<T>{
    private String code = "Unknown code";
    private String message = "Unknown message";
    private T data;
}
