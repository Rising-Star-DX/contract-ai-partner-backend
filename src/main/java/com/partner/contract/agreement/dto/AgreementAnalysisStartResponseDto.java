package com.partner.contract.agreement.dto;

import lombok.Data;

@Data
public class AgreementAnalysisStartResponseDto {
    private Long id;
    private String url;

    public AgreementAnalysisStartResponseDto(Long id, String url) {
        this.id = id;
        this.url = url;
    }
}
