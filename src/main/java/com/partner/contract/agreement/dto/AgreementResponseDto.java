package com.partner.contract.agreement.dto;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.domain.FileType;
import com.partner.contract.agreement.domain.UploadStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AgreementResponseDto {

    private Long id;
    private String name;
    private FileType type;
    private String category;
    private UploadStatus status;
    private LocalDateTime createdAt;

    @Builder
    public AgreementResponseDto(Long id, String name, FileType type, String category, UploadStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static AgreementResponseDto fromEntity(Agreement agreement) {
        return AgreementResponseDto.builder()
                .id(agreement.getId())
                .name(agreement.getName())
                .type(agreement.getType())
                .category(agreement.getCategory().getName())
                .status(agreement.getStatus())
                .createdAt(agreement.getCreatedAt())
                .build();
    }
}
