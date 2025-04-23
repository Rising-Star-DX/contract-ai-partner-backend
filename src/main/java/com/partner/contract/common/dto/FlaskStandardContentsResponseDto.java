package com.partner.contract.common.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FlaskStandardContentsResponseDto {
    private Long standardId;
    private String result;
    private List<String> contents;
}
