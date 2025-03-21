package com.partner.contract.agreement.dto;

import java.util.List;

public class AgreementAnalysisFlaskResponseDto {
    private String clauseContent;
    private List<String> incorrectTexts;
    private List<String> correctedTexts;
    private List<String> proofTexts;
    private Double accuracy;
    private Long page;
}
