package com.partner.contract.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class CategoryListResponseDto {
    private Integer id;
    private String name;
    private Integer countOfStandards;
    private Integer countOfAgreements;
    private String createdAt;

    @Builder
    public CategoryListResponseDto(Integer id, String name, Integer countOfStandards, Integer countOfAgreements, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.countOfStandards = countOfStandards;
        this.countOfAgreements = countOfAgreements;
        this.createdAt = convertTimestampToLocalDateTime(createdAt);
    }

    private String convertTimestampToLocalDateTime(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return timestamp.toLocalDateTime().format(formatter);
    }
}
