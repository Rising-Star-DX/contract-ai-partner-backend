package com.partner.contract.common.service;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileUploadService {
    private final S3Client s3Client;

    @Value("${secret.aws.s3.bucket-name}")
    private String bucketName;

    public String getBucketName() {
        return bucketName;
    }

    public String uploadFile(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) { // try-with-resources 사용
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(inputStream, file.getSize()) // 스트림 방식 적용
            );

            if (response.sdkHttpResponse().isSuccessful()) {
                return fileName;
            } else {
                throw new ApplicationException(ErrorCode.S3_FILE_UPLOAD_ERROR);
            }
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }
}
