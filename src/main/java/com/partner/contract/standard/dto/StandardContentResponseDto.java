package com.partner.contract.standard.dto;

import lombok.Getter;

@Getter
public class StandardContentResponseDto {
    private Integer page;
    private String content;

    public StandardContentResponseDto(Integer page, String content) {
        this.page = page;
        this.content = content;
    }
}
