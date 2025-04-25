package com.partner.contract.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FlaskStandardContentsResponseDto {
    private Long standardId;
    private String result;
    private List<String> contents;
}
