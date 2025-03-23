package com.partner.contract.common.dto;

import com.partner.contract.common.enums.FileType;
import lombok.Builder;
import lombok.Data;

@Data
public class AnalysisRequestDto {
    private Long id;
    private String url;
    private String categoryName;
    private FileType type;

    @Builder
    public AnalysisRequestDto(Long id, String url, String categoryName, FileType type) {
        this.id = id;
        this.url = url;
        this.categoryName = categoryName;
        this.type = type;
    }
}
