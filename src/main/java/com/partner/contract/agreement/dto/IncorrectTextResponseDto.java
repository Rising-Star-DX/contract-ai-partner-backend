package com.partner.contract.agreement.dto;

import lombok.Data;

@Data
public class IncorrectTextResponseDto {
    private Long id;
    private Integer page;
    private Double accuracy;
    private String incorrectText;
    private String proofText;
    private String correctedText;

    public IncorrectTextResponseDto(Long id, Integer page, Double accuracy, String incorrectText, String proofText, String correctedText) {
        this.id = id;
        this.page = page;
        this.accuracy = accuracy;
        this.incorrectText = incorrectText;
        this.proofText = proofText;
        this.correctedText = correctedText;
    }
}
