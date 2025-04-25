package com.partner.contract.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class AgreementIncorrectDto {
    private String incorrectText;
    private String proofText;
    private String correctedText;
    private Double accuracy;
    @JsonProperty("clauseData")
    private List<IncorrectClauseDataDto> incorrectClauseDataDtoList;
}
