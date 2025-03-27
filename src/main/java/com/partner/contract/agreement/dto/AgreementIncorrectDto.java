package com.partner.contract.agreement.dto;

import lombok.Data;

import java.util.List;


@Data
public class AgreementIncorrectDto {
    private Integer page;
    private Integer orderIndex;
    private String incorrectText;
    private String proofText;
    private String correctedText;
    private Double accuracy;
    private List<String> position;
}
