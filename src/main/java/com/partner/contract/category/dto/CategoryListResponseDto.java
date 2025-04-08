package com.partner.contract.category.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class CategoryListResponseDto {
    private Long id;
    private String name;
    private Long countOfStandards;
    private Long countOfAgreements;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Timestamp createdAt;

    @Builder
    public CategoryListResponseDto(Long id, String name, Long countOfStandards, Long countOfAgreements, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.countOfStandards = countOfStandards;
        this.countOfAgreements = countOfAgreements;
        this.createdAt = createdAt;
    }
}
