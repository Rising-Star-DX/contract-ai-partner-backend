package com.partner.contract.agreement.dto;

import com.partner.contract.agreement.common.enums.AiStatus;
import com.partner.contract.agreement.common.enums.FileType;
import com.partner.contract.agreement.domain.Agreement;
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
    private AiStatus aiStatus;
    private LocalDateTime createdAt;

    @Builder
    public AgreementResponseDto(Long id, String name, FileType type, String category, AiStatus aiStatus, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.aiStatus = aiStatus;
        this.createdAt = createdAt;
    }

    public static AgreementResponseDto fromEntity(Agreement agreement) {
        return AgreementResponseDto.builder()
                .id(agreement.getId())
                .name(agreement.getName())
                .type(agreement.getType())
                .category(agreement.getCategory().getName())
                .aiStatus(agreement.getAiStatus())
                .createdAt(agreement.getCreatedAt())
                .build();
    }
}
