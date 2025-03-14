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
public class StandardResponseDto {

    private Long id;
    private String name;
    private FileType type;
    private String url;
    private String status;
    private LocalDateTime createdAt;

    @Builder
    public StandardResponseDto(Long id, String name, FileType type, String url, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static StandardResponseDto fromEntity(Standard standard) {
        return StandardResponseDto.builder()
                .id(standard.getId())
                .name(standard.getName())
                .type(standard.getType())
                .url(standard.getUrl())
                .status(DocumentStatusUtil.determineStatus(standard.getFileStatus(), standard.getAiStatus()))
                .createdAt(standard.getCreatedAt())
                .build();
    }
}
