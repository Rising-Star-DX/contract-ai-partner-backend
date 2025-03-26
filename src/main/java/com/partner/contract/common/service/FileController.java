package com.partner.contract.common.service;

import org.springframework.http.*;
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
//        byte[] convertedFileBytes = yourService.convertFileToPdf(file);
//
//        // HTTP 응답 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);  // PDF로 지정
//        headers.setContentDisposition(ContentDisposition.attachment().filename("converted.pdf").build());  // 다운로드 파일명 지정
//
//        return new ResponseEntity<>(convertedFileBytes, headers, HttpStatus.OK);
        return null;
    }
}