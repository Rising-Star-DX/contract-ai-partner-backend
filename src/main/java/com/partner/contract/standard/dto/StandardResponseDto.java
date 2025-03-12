package com.partner.contract.standard.dto;

import com.partner.contract.agreement.common.enums.AiStatus;
import com.partner.contract.agreement.common.enums.FileType;
import com.partner.contract.standard.domain.Standard;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StandardResponseDto {

    private Long id;
    private String name;
    private FileType type;
    private String category;
    private AiStatus aiStatus;
    private LocalDateTime createdAt;

    @Builder
    public StandardResponseDto(Long id, String name, FileType type, String category, AiStatus aiStatus, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.aiStatus = aiStatus;
        this.createdAt = createdAt;
    }

    public static StandardResponseDto fromEntity(Standard standard) {
        return StandardResponseDto.builder()
                .id(standard.getId())
                .name(standard.getName())
                .type(standard.getType())
                .category(standard.getCategory().getName())
                .aiStatus(standard.getAiStatus())
                .createdAt(standard.getCreatedAt())
                .build();
    }
}
