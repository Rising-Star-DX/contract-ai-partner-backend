package com.partner.contract.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AgreementAnalysisFlaskResponseDto {
    private Integer totalPage;

    private Integer totalChunks;

    @JsonProperty("chunks")
    private List<AgreementIncorrectDto> agreementIncorrectDtos;
}
