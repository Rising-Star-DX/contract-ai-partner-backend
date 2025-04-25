package com.partner.contract.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AgreementAnalysisFlaskResponseDto {
    private Long agreementId;
    private String result;
    private Integer totalPage;
    private Integer totalChunks;
    @JsonProperty("chunks")
    private List<AgreementIncorrectDto> agreementIncorrectDtos;
}
