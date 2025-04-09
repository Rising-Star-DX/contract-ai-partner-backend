package com.partner.contract.category.dto;

import com.partner.contract.category.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryNameListResponseDto {
    private Long id;
    private String name;

    @Builder
    public CategoryNameListResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryNameListResponseDto fromEntity(Category category) {
        return CategoryNameListResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
