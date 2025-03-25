package com.partner.contract.agreement.dto;

import lombok.Data;

import java.util.List;

@Data
public class AgreementChunkDto {
    private String clauseContent;
    private Integer page;
    private List<String> incorrectTexts;
    private List<String> proofTexts;
    private List<String> correctedTexts;
    private String position;
}
