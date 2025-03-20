package com.partner.contract.standard.controller;

import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import com.partner.contract.common.dto.FileUploadInitRequestDto;
import com.partner.contract.standard.dto.StandardListResponseDto;
import com.partner.contract.standard.dto.StandardResponseDto;
import com.partner.contract.standard.service.StandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

    @DeleteMapping("/upload/{id}")
    public ResponseEntity<SuccessResponse<String>> standardFileUploadCancel(@PathVariable("id") Long id){
        standardService.cancelFileUpload(id);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<StandardResponseDto>> standardById(@PathVariable("id") Long id) {
        StandardResponseDto standard = standardService.findStandardById(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> standardDelete(@PathVariable("id") Long id) {
        standardService.deleteStandard(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), null));
    }

    @PostMapping("/upload/{category-id}")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> standardFileUpload(
            @RequestPart("file") MultipartFile file,
            @PathVariable("category-id") Long categoryId) {

        Long id = standardService.uploadFile(file, categoryId);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.INSERT_SUCCESS.getCode(),
                SuccessCode.INSERT_SUCCESS.getMessage(),
                Map.of("id", id)));
    }

    @PatchMapping("/analysis/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> standardAnalysis(@PathVariable("id") Long id){

        standardService.analyze(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.UPDATE_SUCCESS.getCode(),
                SuccessCode.UPDATE_SUCCESS.getMessage(),
                Map.of("id", id)));
    }
}
