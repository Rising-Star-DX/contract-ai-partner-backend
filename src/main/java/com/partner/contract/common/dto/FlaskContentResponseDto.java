package com.partner.contract.common.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FlaskContentResponseDto {
    private String result;
    private List<String> contents;
}
