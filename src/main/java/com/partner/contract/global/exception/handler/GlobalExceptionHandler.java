package com.partner.contract.global.exception.handler;

import com.partner.contract.global.exception.dto.ErrorResponse;
import com.partner.contract.global.exception.error.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<ErrorResponse> handlerApplicationException(ApplicationException e) {
        log.error("{}: {}", e.getCode(), e.getMessage(), e);
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.of(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handlerRuntimeException(RuntimeException e) {
        log.error("{}: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of("RT", e.getMessage())); // runtime error
    }
}
