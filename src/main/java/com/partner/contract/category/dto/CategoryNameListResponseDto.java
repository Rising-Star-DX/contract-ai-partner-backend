package com.partner.contract.category.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.partner.contract.category.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class CategoryNameListResponseDto {
    private Long id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Timestamp createdAt;

    @Builder
    public CategoryNameListResponseDto(Long id, String name, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static CategoryNameListResponseDto fromEntity(Category category) {
        return CategoryNameListResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .createdAt(Timestamp.valueOf(category.getCreatedAt()))
                .build();
    }
}
