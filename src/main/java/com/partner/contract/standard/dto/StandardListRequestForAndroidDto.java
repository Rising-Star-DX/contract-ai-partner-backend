package com.partner.contract.standard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.partner.contract.common.enums.FileType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StandardListRequestForAndroidDto {

    private String name;
    private List<FileType> type;
    private List<String> status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    private Long categoryId;
    private List<String> sortBy;
    private List<Boolean> asc;
}
