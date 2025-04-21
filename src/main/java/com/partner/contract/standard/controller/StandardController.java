package com.partner.contract.standard.controller;

import com.partner.contract.common.enums.FileType;
import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import com.partner.contract.standard.dto.StandardDetailsResponseDto;
import com.partner.contract.standard.dto.StandardDetailsResponseForAdminDto;
import com.partner.contract.standard.dto.StandardListRequestForAndroidDto;
import com.partner.contract.standard.dto.StandardListResponseDto;
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
    public ResponseEntity<SuccessResponse<List<StandardListResponseDto>>> standardList(
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "category-id", required = false) Long categoryId) {
        List<StandardListResponseDto> standards = standardService.findStandardList(name, categoryId);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standards));
    }

    @GetMapping("/android")
    public ResponseEntity<SuccessResponse<List<StandardListResponseDto>>> standardListAndroid(
            @RequestParam(name = "status", required = false) List<String> status,
            @RequestParam(name = "type", required = false) List<FileType> type,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "sortBy", required = false) List<String> sortBy,
            @RequestParam(name = "asc", required = false) List<Boolean> asc
    ) {
        StandardListRequestForAndroidDto requestForAndroidDto = StandardListRequestForAndroidDto.builder()
                .name(name)
                .type(type)
                .categoryId(categoryId)
                .status(status)
                .sortBy(sortBy)
                .asc(asc)
                .build();

        List<StandardListResponseDto> standards = standardService.findStandardListForAndroid(requestForAndroidDto);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standards));
    }

    @DeleteMapping("/upload/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> standardFileUploadCancel(@PathVariable("id") Long id){
        standardService.cancelFileUpload(id);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), Map.of("id", id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<StandardDetailsResponseDto>> standardById(@PathVariable("id") Long id) {
        StandardDetailsResponseDto standard = standardService.findStandardById(id);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standard));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<SuccessResponse<StandardDetailsResponseForAdminDto>> standardByIdForAdmin(@PathVariable("id") Long id) {
        StandardDetailsResponseForAdminDto standard = standardService.findStandardByIdForAdmin(id);
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

        standardService.startAnalyze(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.ANALYSIS_REQUEST_ACCEPTED.getCode(),
                SuccessCode.ANALYSIS_REQUEST_ACCEPTED.getMessage(),
                Map.of("id", id)));
    }

    @GetMapping("/analysis/check/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Boolean>>> standardCheckAnalysisCompletion(@PathVariable("id") Long id){
        Boolean isCompleted = standardService.checkAnalysisCompleted(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), Map.of("isCompletion", isCompleted)));
    }
}
