package com.partner.contract.global.exception.controller;

import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/success")
    public ResponseEntity<SuccessResponse<String>> success() {
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS, null));
    }
}
