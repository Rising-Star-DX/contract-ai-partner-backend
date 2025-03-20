package com.partner.contract.agreement.controller;

import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.service.AgreementService;
import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import com.partner.contract.common.dto.FileUploadInitRequestDto;
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
    public ResponseEntity<SuccessResponse<List<AgreementListResponseDto>>> agreementList() {
        List<AgreementListResponseDto> agreements = agreementService.findAgreementList();

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), agreements));
    }

    @GetMapping(params = "name")
    public ResponseEntity<SuccessResponse<List<AgreementListResponseDto>>> agreementListByName(@RequestParam("name") String name) {
        List<AgreementListResponseDto> agreements = agreementService.findAgreementListByName(name);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), agreements));
    }

    @GetMapping(params = "category-id")
    public ResponseEntity<SuccessResponse<List<AgreementListResponseDto>>> agreementListByCategoryId(@RequestParam("category-id") Long categoryId) {
        List<AgreementListResponseDto> agreements = agreementService.findAgreementListByCategoryId(categoryId);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), agreements));
    }

    @DeleteMapping("/upload/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> agreementFileUploadCancel(@PathVariable("id") Long id){
        agreementService.cancelFileUpload(id);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), Map.of("id", id)));
    }

    @PostMapping("/upload/{category-id}")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> standardFileUpload(
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
}
