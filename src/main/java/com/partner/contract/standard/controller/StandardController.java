package com.partner.contract.standard.controller;

import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import com.partner.contract.standard.dto.StandardResponseDto;
import com.partner.contract.standard.service.StandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/standards")
public class StandardController {
    private final StandardService standardService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<StandardResponseDto>>> standardList() {
        List<StandardResponseDto> standards = standardService.findStandardList();

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standards));
    }
}
