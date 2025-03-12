package com.partner.contract.standard.dto;

import com.partner.contract.agreement.domain.FileType;
import com.partner.contract.agreement.domain.UploadStatus;
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
    private UploadStatus status;
    private LocalDateTime createdAt;

    @Builder
    public StandardResponseDto(Long id, String name, FileType type, String category, UploadStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static StandardResponseDto fromEntity(Standard standard) {
        return StandardResponseDto.builder()
                .id(standard.getId())
                .name(standard.getName())
                .type(standard.getType())
                .category(standard.getCategory().getName())
                .status(standard.getStatus())
                .createdAt(standard.getCreatedAt())
                .build();
    }
}
