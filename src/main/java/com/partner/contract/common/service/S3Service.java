package com.partner.contract.common.service;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${secret.aws.s3.bucket-name}")
    private String bucketName;

    @Value("${secret.aws.s3.region}")
    private String region;

    public String getBucketName() {
        return bucketName;
    }

    public String uploadFile(MultipartFile file, String folder) {

        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

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
                return makeFileURL(fileName);
            } else {
                throw new ApplicationException(ErrorCode.S3_FILE_UPLOAD_ERROR);
            }
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (S3Exception e) {
            throw new ApplicationException(ErrorCode.S3_CONNECTION_ERROR);
        }
    }

    public void deleteFile(String url) {
        String filePath = url.split(".amazonaws.com")[1].substring(1);

        if (!isFileExists(filePath)) {
            throw new ApplicationException(ErrorCode.S3_FILE_NOT_FOUND_ERROR);
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new ApplicationException(ErrorCode.S3_CONNECTION_ERROR);
        }
    }

    private boolean isFileExists(String filePath) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true; // 파일이 존재함
        } catch (NoSuchKeyException e) {
            return false; // 파일이 존재하지 않음 (삭제 성공)
        } catch (S3Exception e) {
            throw new ApplicationException(ErrorCode.S3_CONNECTION_ERROR);
        }
    }

    private String makeFileURL(String fileName) {
        return "https://" + bucketName + ".s3."+ region +  ".amazonaws.com/" + fileName;
    }
}
