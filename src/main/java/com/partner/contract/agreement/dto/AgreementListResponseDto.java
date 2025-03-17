package com.partner.contract.agreement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.utils.DocumentStatusUtil;
import com.partner.contract.agreement.domain.Agreement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AgreementListResponseDto {

    private Long id;
    private String name;
    private FileType type;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @Builder
    public AgreementListResponseDto(Long id, String name, FileType type, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static AgreementListResponseDto fromEntity(Agreement agreement) {
        return AgreementListResponseDto.builder()
                .id(agreement.getId())
                .name(agreement.getName())
                .type(agreement.getType())
                .status(DocumentStatusUtil.determineStatus(agreement.getFileStatus(), agreement.getAiStatus()))
                .createdAt(agreement.getCreatedAt())
                .build();
    }
}
