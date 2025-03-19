package com.partner.contract.common.dto;

import com.partner.contract.common.enums.FileType;
import lombok.Data;

@Data
public class FileUploadInitRequestDto {
    private String name; // 문서 이름
    private FileType type; // 문서 타입
    private Long categoryId; // 카테고리 ID
}
