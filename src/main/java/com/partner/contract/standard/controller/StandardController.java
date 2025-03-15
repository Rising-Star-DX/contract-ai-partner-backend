package com.partner.contract.standard.controller;

import com.partner.contract.common.dto.FileUploadRequestDto;
import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import com.partner.contract.standard.dto.FileUploadInitRequestDto;
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
@CrossOrigin(origins = "http://localhost:3000")
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

    @PostMapping("/upload/init")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> standardFileUploadInit(@RequestBody FileUploadInitRequestDto requestDto) {
        Long id = standardService.initFileUpload(requestDto);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.INSERT_SUCCESS.getCode(), SuccessCode.INSERT_SUCCESS.getMessage(), Map.of("id", id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<StandardResponseDto>> standardById(@PathVariable("id") Long id) {
        StandardResponseDto standard = standardService.findStandardById(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), standard));
    }

    @PostMapping("/upload")
    public ResponseEntity<SuccessResponse<Map<String, Long>>> standardFileUpload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") FileUploadRequestDto data) {

        Long id = standardService.uploadFile(file, data);
//        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.INSERT_SUCCESS.getCode(),
//                SuccessCode.INSERT_SUCCESS.getMessage(),
//                Map.of("id", id)));

        return null;
    }
}
