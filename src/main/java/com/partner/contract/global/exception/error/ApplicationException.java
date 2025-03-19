package com.partner.contract.global.exception.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException{
    private final HttpStatus status;
    private final String code;

    public ApplicationException(ErrorCode error) {
        super(error.getMessage());
        this.status = error.getStatus();
        this.code = error.getCode();
    }

    public ApplicationException(ErrorCode error, String message) {
        super(message);
        this.status = error.getStatus();
        this.code = error.getCode();
    }
}
