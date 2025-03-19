package com.partner.contract.standard.service;

import com.partner.contract.category.domain.Category;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.common.dto.AnalysisRequestDto;
import com.partner.contract.common.dto.FileUploadInitRequestDto;
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.partner.contract.common.service.S3FileUploadService;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.dto.StandardListResponseDto;
import com.partner.contract.standard.dto.StandardResponseDto;
import com.partner.contract.standard.repository.StandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StandardService {
    private final StandardRepository standardRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;
    private final S3FileUploadService s3FileUploadService;

    @Value("${secret.flask.ip}")
    private String FLASK_SERVER_IP;

    public List<StandardListResponseDto> findStandardList() {
        return standardRepository.findAllWithCategoryByOrderByCreatedAtDesc()
                .stream()
                .map(StandardListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<StandardListResponseDto> findStandardListByName(String name) {
        return standardRepository.findWithCategoryByNameContaining(name)
                .stream()
                .map(StandardListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<StandardListResponseDto> findStandardListByCategoryId(Long categoryId) {
        List<Standard> standards = standardRepository.findWithCategoryByCategoryId(categoryId);
        if (standards.isEmpty()) {
            throw new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR);
        }
        return standards
                .stream()
                .map(StandardListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Long initFileUpload(FileUploadInitRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

        Standard standard = Standard.builder()
                .name(requestDto.getName())
                .type(requestDto.getType())
                .category(category)
                .build();

        return standardRepository.save(standard).getId();
    }

    public StandardResponseDto findStandardById(Long id) {
        Standard standard = standardRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));
        return StandardResponseDto.fromEntity(standard);
    }

    public void deleteStandard(Long id) {
        Standard standard = standardRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        String flaskUrl = FLASK_SERVER_IP + "/flask/standards/" + id;

        try {
            ResponseEntity<FlaskResponseDto<String>> response = restTemplate.exchange(
                    flaskUrl,
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<FlaskResponseDto<String>>() {} // ✅ 제네릭 타입 유지
            );

            FlaskResponseDto<String> body = response.getBody();

            // Flask 응답 검증 추가
            if (body == null) {
                throw new ApplicationException(ErrorCode.FLASK_SERVER_ERROR);
            }

            if (body.getData() == null) {
                throw new ApplicationException(ErrorCode.FLASK_SERVER_ERROR);
            }

            if ("success".equals(body.getData())) {
                standardRepository.delete(standard);
            } else {
                throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "F-" + body.getCode(), body.getMessage());
            }
        } catch (RestClientException e) {
            throw new ApplicationException(ErrorCode.FLASK_SERVER_CONNECTION_ERROR);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED) // FAIL 예외 처리를 위해 NOT_SUPPORTED로 설정
    public void uploadFile(MultipartFile file, Long id) {

        Standard standard = standardRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        String fileName = null;
        try {
            fileName = s3FileUploadService.uploadFile(file);
        } catch (ApplicationException e) {
            standard.updateFileStatus(null, FileStatus.FAILED, null);
            standardRepository.save(standard);
            throw e; // 예외 다시 던지기
        }
        String url = "s3://" + s3FileUploadService.getBucketName() + "/" + fileName;
        standard.updateFileStatus(url, FileStatus.SUCCESS, AiStatus.ANALYZING);
        standardRepository.save(standard);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED) // FAIL 예외 처리를 위해 NOT_SUPPORTED로 설정
    public void analyze(Long id) {
        Standard standard = standardRepository.findWithCategoryById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        if (standard.getFileStatus() != FileStatus.SUCCESS) {
            throw new ApplicationException(ErrorCode.MISSING_FILE_FOR_ANALYSIS);
        }

        // Flask에 AI 분석 요청
        String url = FLASK_SERVER_IP + "/flask/standards/analysis";

        AnalysisRequestDto analysisRequestDto = AnalysisRequestDto.builder()
                .id(standard.getId())
                .url(standard.getUrl())
                .categoryName(standard.getCategory().getName())
                .type(standard.getType())
                .build();

        // HTTP Request Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP Request Body 설정
        HttpEntity<AnalysisRequestDto> requestEntity = new HttpEntity<>(analysisRequestDto, headers);

        try {
            // Flask에 API 요청
            ResponseEntity<FlaskResponseDto<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<FlaskResponseDto<String>>() {} // ✅ 제네릭 타입 유지
            );

            FlaskResponseDto<String> body = response.getBody();

            // Flask 응답 검증 추가
            if (body == null) {
                standard.updateAiStatus(AiStatus.FAILED);
                throw new ApplicationException(ErrorCode.FLASK_SERVER_ERROR);
            }

            if (body.getData() == null) {
                standard.updateAiStatus(AiStatus.FAILED);
                throw new ApplicationException(ErrorCode.FLASK_SERVER_ERROR);
            }

            if ("success".equals(body.getData())) { // 기준문서 분석 성공
                standard.updateAiStatus(AiStatus.SUCCESS);
                standardRepository.save(standard);
            } else {
                standard.updateAiStatus(AiStatus.FAILED);
                throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "F-" + body.getCode(), body.getMessage());
            }
        } catch (RestClientException e) {
            standard.updateAiStatus(AiStatus.FAILED);
            throw new ApplicationException(ErrorCode.FLASK_SERVER_CONNECTION_ERROR);
        }
    }
}
