package com.partner.contract.standard.controller;

import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import com.partner.contract.standard.dto.StandardListResponseDto;
import com.partner.contract.standard.dto.StandardResponseDto;
import com.partner.contract.standard.service.StandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/standards")
public class StandardController {
    private final StandardService standardService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<StandardListResponseDto>>> standardList() {
        List<StandardListResponseDto> standards = standardService.findStandardList();

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standards));
    }

    @GetMapping(params = "name")
    public ResponseEntity<SuccessResponse<List<StandardListResponseDto>>> standardListByName(@RequestParam("name") String name) {
        List<StandardListResponseDto> standards = standardService.findStandardListByName(name);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standards));
    }

    @GetMapping(params = "category-id")
    public ResponseEntity<SuccessResponse<List<StandardListResponseDto>>> standardListByCategoryId(@RequestParam("category-id") Long categoryId) {
        List<StandardListResponseDto> standards = standardService.findStandardListByCategoryId(categoryId);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standards));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<StandardResponseDto>> standardById(@PathVariable("id") Long id) {
        StandardResponseDto standard = standardService.findStandardById(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standard));
    }
}
