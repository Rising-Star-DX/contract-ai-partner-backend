package com.partner.contract.standard.dto;

import com.partner.contract.agreement.common.enums.AiStatus;
import com.partner.contract.agreement.common.enums.FileStatus;
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
    private String status;
    private LocalDateTime createdAt;

    @Builder
    public StandardResponseDto(Long id, String name, FileType type, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static StandardResponseDto fromEntity(Standard standard) {
        return StandardResponseDto.builder()
                .id(standard.getId())
                .name(standard.getName())
                .type(standard.getType())
                .status(determineStatus(standard.getFileStatus(), standard.getAiStatus()))
                .createdAt(standard.getCreatedAt())
                .build();
    }

    private static String determineStatus(FileStatus fileStatus, AiStatus aiStatus) {
        if (fileStatus == FileStatus.UPLOADING && aiStatus == null) {
            return "UPLOADING";
        } else if (fileStatus == FileStatus.FAILED && aiStatus == null) {
            return "FILEFAILED";
        } else if (fileStatus == FileStatus.SUCCESS && aiStatus == AiStatus.ANALYZING) {
            return "ANALYZING";
        } else if (fileStatus == FileStatus.SUCCESS && aiStatus == AiStatus.FAILED) {
            return "AIFAILED";
        } else if (fileStatus == FileStatus.SUCCESS && aiStatus == AiStatus.SUCCESS) {
            return "SUCCESS";
        }
        return null;
    }

}
