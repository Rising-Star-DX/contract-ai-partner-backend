package com.partner.contract.standard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.utils.DocumentStatusUtil;
import com.partner.contract.standard.domain.Standard;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StandardListResponseDto {

    private Long id;
    private String name;
    private FileType type;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    private String categoryName;

    @Builder
    public StandardListResponseDto(Long id, String name, FileType type, String status, LocalDateTime createdAt, String categoryName) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.categoryName = categoryName;
    }

    public static StandardListResponseDto fromEntity(Standard standard) {
        return StandardListResponseDto.builder()
                .id(standard.getId())
                .name(standard.getName())
                .type(standard.getType())
                .status(DocumentStatusUtil.determineStatus(standard.getFileStatus(), standard.getAiStatus()))
                .createdAt(standard.getCreatedAt())
                .categoryName(standard.getCategory().getName())
                .build();
    }
}
