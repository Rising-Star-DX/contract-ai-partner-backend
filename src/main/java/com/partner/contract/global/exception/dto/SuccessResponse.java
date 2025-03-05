package com.partner.contract.global.exception.dto;

import com.partner.contract.global.exception.error.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {
    private String code; // 응답 코드
    private String message; // 응답 코드에 대한 상세 메세지
    private T data; // 전달할 데이터

    public static <T> SuccessResponse<T> of(String code, String message, T data) {
        return new SuccessResponse<>(code, message, data);
    }

    public static <T> SuccessResponse<T> of(SuccessCode successCode, T data) {
        return new SuccessResponse<>(successCode.getCode(), successCode.getMessage(), data);
    }
}
