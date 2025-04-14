package com.partner.contract.standard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.partner.contract.common.enums.FileType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class StandardResponseForAdminDto {

    private Long id;
    private String name;
    private FileType type;
    private String url;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    private String categoryName;
    @JsonProperty("contents")
    private List<StandardContentResponseDto> standardContentResponseDtoList;

    @Builder
    public StandardResponseForAdminDto(Long id, String name, FileType type, String url, String status, LocalDateTime createdAt, String categoryName, List<StandardContentResponseDto> standardContentResponseDtoList) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.status = status;
        this.createdAt = createdAt;
        this.categoryName = categoryName;
        this.standardContentResponseDtoList = standardContentResponseDtoList;
    }
}
