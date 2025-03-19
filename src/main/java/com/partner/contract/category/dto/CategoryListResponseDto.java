package com.partner.contract.category.dto;

import com.partner.contract.category.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryListResponseDto {
    private Long id;
    private String name;

    @Builder
    public CategoryListResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryListResponseDto fromEntity(Category category) {
        return CategoryListResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
