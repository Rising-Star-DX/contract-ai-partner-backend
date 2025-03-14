package com.partner.contract.standard.dto;

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
    private LocalDateTime createdAt;

    @Builder
    public StandardListResponseDto(Long id, String name, FileType type, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static StandardListResponseDto fromEntity(Standard standard) {
        return StandardListResponseDto.builder()
                .id(standard.getId())
                .name(standard.getName())
                .type(standard.getType())
                .status(DocumentStatusUtil.determineStatus(standard.getFileStatus(), standard.getAiStatus()))
                .createdAt(standard.getCreatedAt())
                .build();
    }
}
