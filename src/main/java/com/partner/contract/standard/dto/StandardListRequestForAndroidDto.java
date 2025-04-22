package com.partner.contract.standard.dto;

import com.partner.contract.common.enums.FileType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StandardListRequestForAndroidDto {

    private String name;
    private List<FileType> type;
    private List<String> status;
    private Long categoryId;
    private List<String> sortBy;
    private List<Boolean> asc;

    @Builder
    public StandardListRequestForAndroidDto(String name, List<FileType> type, List<String> status, Long categoryId, List<String> sortBy, List<Boolean> asc) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.categoryId = categoryId;
        this.sortBy = sortBy;
        this.asc = asc;
    }
}
