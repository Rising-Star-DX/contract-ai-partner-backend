package com.partner.contract.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class AgreementAnalysisReportDetailsResponseDto {

    private Long id;
    private String name;
    private String categoryName;
    private Integer totalPage;
    private Integer totalChunks;
    private Integer countOfIncorrectTexts;
    @JsonProperty("incorrectTexts")
    private List<IncorrectTextAnalysisReportResponseDto> incorrectTextAnalysisReportResponseDtoList;

    @Builder
    public AgreementAnalysisReportDetailsResponseDto(Long id, String name, String categoryName, Integer totalPage, Integer totalChunks, List<IncorrectTextAnalysisReportResponseDto> incorrectTextAnalysisReportResponseDtoList) {
        this.id = id;
        this.name = name;
        this.categoryName = categoryName;
        this.totalPage = totalPage;
        this.totalChunks = totalChunks;
        this.countOfIncorrectTexts = incorrectTextAnalysisReportResponseDtoList.size();
        this.incorrectTextAnalysisReportResponseDtoList = incorrectTextAnalysisReportResponseDtoList;
    }
}
