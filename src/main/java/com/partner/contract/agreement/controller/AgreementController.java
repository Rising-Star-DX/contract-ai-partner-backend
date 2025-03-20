package com.partner.contract.agreement.controller;

import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.service.AgreementService;
import com.partner.contract.common.dto.FileUploadInitRequestDto;
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
            @RequestParam(name = "category-id", required = false, defaultValue = "0") Long categoryId) {
        List<AgreementListResponseDto> agreements = agreementService.findAgreementList(name, categoryId);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), agreements));
    }

    @PostMapping("/upload/init")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> agreementFileUploadInit(@RequestBody FileUploadInitRequestDto requestDto) {
        Long id = agreementService.initFileUpload(requestDto);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.INSERT_SUCCESS.getCode(), SuccessCode.INSERT_SUCCESS.getMessage(), Map.of("id", id)));
    }

    @DeleteMapping("/upload/{id}")
    public ResponseEntity<SuccessResponse<String>> agreementFileUploadCancel(@PathVariable("id") Long id){
        agreementService.cancelFileUpload(id);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), null));
    }

    @PatchMapping("/upload/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> standardFileUpload(
            @RequestPart("file") MultipartFile file,
            @PathVariable("id") Long id) {

        agreementService.uploadFile(file, id);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.UPDATE_SUCCESS.getCode(),
                SuccessCode.UPDATE_SUCCESS.getMessage(),
                Map.of("id", id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> agreementDelete(@PathVariable("id") Long id) {
        agreementService.deleteAgreement(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), null));
    }
}
