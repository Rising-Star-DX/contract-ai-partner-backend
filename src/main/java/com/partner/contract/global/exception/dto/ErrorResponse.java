package com.partner.contract.global.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code; // 응답 코드
    private String message; // 응답 코드에 대한 상세 메세지

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }
}
