package com.partner.contract.common.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    private final FileConversionService yourService; // 변환 서비스

    public FileController(FileConversionService yourService) {
        this.yourService = yourService;
    }

    @PostMapping("/convert-to-pdf")
    public ResponseEntity<byte[]> convertToPdf(@RequestParam("file") MultipartFile file) {
        // 변환된 PDF 파일을 byte[]로 반환
        byte[] convertedFileBytes = yourService.convertFileToPdf(file);

        // 파일 다운로드 응답 헤더 설정
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "test")
                .contentType(MediaType.APPLICATION_PDF)  // Content-Type을 application/pdf로 설정
                .body(convertedFileBytes);
    }
}