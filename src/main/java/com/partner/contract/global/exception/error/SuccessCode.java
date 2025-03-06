package com.partner.contract.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    SELECT_SUCCESS(HttpStatus.OK, "S001", "SELECT SUCCESS"),
    INSERT_SUCCESS(HttpStatus.CREATED, "S002", "INSERT SUCCESS"),
    UPDATE_SUCCESS(HttpStatus.OK, "S003", "UPDATE SUCCESS"),
    DELETE_SUCCESS(HttpStatus.OK, "S004", "DELETE SUCCESS");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
