package com.partner.contract.agreement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.partner.contract.common.enums.FileType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AgreementDetailsResponseDto {

    private Long id;
    private String name;
    private FileType type;
    private String url;
    private String status;
    private String categoryName;
    private Integer totalPage;
    @JsonProperty("incorrectTexts")
    private List<IncorrectTextResponseDto> incorrectTextResponseDtoList;

    @Builder
    public AgreementDetailsResponseDto(Long id, String name, FileType type, String url, String status, String categoryName, Integer totalPage, List<IncorrectTextResponseDto> incorrectTextResponseDtoList) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.status = status;
        this.categoryName = categoryName;
        this.totalPage = totalPage;
        this.incorrectTextResponseDtoList = incorrectTextResponseDtoList;
    }
}
