package com.partner.contract.agreement.controller;

import com.partner.contract.agreement.dto.AgreementAnalysisStartResponseDto;
import com.partner.contract.agreement.dto.AgreementAnalysisReportDetailsResponseDto;
import com.partner.contract.agreement.dto.AgreementDetailsResponseDto;
import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.service.AgreementService;
import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/agreements")
public class AgreementController {
    private final AgreementService agreementService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<AgreementListResponseDto>>> agreementList(
            @RequestParam(name = "name", required = false, defaultValue = "") String name,
            @RequestParam(name = "category-id", required = false) Long categoryId) {
        List<AgreementListResponseDto> agreements = agreementService.findAgreementList(name, categoryId);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), agreements));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<AgreementDetailsResponseDto>> agreementDetails(@PathVariable("id") Long id) {
        AgreementDetailsResponseDto agreementDetails = agreementService.findAgreementDetailsById(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), agreementDetails));
    }

    @GetMapping("/analysis/{id}")
    public ResponseEntity<SuccessResponse<AgreementAnalysisReportDetailsResponseDto>> agreementAnalysisReportDetails(@PathVariable("id") Long id) {
        AgreementAnalysisReportDetailsResponseDto agreementAnalysisReportDetails = agreementService.findAgreementAnalysisReportDetails(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), agreementAnalysisReportDetails));
    }

    @DeleteMapping("/upload/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> agreementFileUploadCancel(@PathVariable("id") Long id){
        agreementService.cancelFileUpload(id);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), Map.of("id", id)));
    }

    @PostMapping("/upload/{category-id}")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> agreementFileUpload(
            @RequestPart("file") MultipartFile file,
            @PathVariable("category-id") Long categoryId) {

        Long id = agreementService.uploadFile(file, categoryId);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.INSERT_SUCCESS.getCode(),
                SuccessCode.INSERT_SUCCESS.getMessage(),
                Map.of("id", id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> agreementDelete(@PathVariable("id") Long id) {
        agreementService.deleteAgreement(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), null));
    }

    @GetMapping("/analysis/check/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Boolean>>> agreementCheckAnalysisCompletion(@PathVariable("id") Long id){
        Boolean isCompleted = agreementService.checkAnalysisCompleted(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), Map.of("isCompletion", isCompleted)));
    }

    @PatchMapping("/analysis/{id}")
    public ResponseEntity<SuccessResponse<AgreementAnalysisStartResponseDto>> agreementAnalysis(@PathVariable("id") Long id){

        AgreementAnalysisStartResponseDto agreementAnalysisStartResponseDto = agreementService.startAnalyze(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.ANALYSIS_REQUEST_ACCEPTED.getCode(),
                SuccessCode.ANALYSIS_REQUEST_ACCEPTED.getMessage(), agreementAnalysisStartResponseDto));
    }
}
