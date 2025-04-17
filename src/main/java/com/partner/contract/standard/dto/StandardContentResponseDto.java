package com.partner.contract.standard.dto;

import lombok.Getter;

@Getter
public class StandardContentResponseDto {
    private Long id;
    private Integer page;
    private String content;

    public StandardContentResponseDto(Long id, Integer page, String content) {
        this.id = id;
        this.page = page;
        this.content = content;
    }
}
