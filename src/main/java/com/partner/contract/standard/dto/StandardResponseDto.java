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
public class StandardResponseDto {

    private Long id;
    private String name;
    private FileType type;
    private String url;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    private String categoryName;

    @Builder
    public StandardResponseDto(Long id, String name, FileType type, String url, String status, LocalDateTime createdAt, String categoryName) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
        this.categoryName = categoryName;
    }

    public static StandardResponseDto fromEntity(Standard standard) {
        return StandardResponseDto.builder()
                .id(standard.getId())
                .name(standard.getName())
                .type(standard.getType())
                .url(standard.getUrl())
                .status(DocumentStatusUtil.determineStatus(standard.getFileStatus(), standard.getAiStatus()))
                .createdAt(standard.getCreatedAt())
                .categoryName(standard.getCategory().getName())
                .build();
    }
}
