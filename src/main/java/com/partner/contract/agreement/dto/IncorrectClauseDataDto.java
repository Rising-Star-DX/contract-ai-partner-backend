package com.partner.contract.agreement.dto;

import lombok.Data;

import java.util.List;

@Data
public class IncorrectClauseDataDto {
    private List<String> position;
    private Integer page;
    private Integer orderIndex;
}
