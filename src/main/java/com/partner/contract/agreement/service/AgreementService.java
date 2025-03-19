package com.partner.contract.agreement.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.repository.AgreementRepository;
import com.partner.contract.category.domain.Category;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.partner.contract.common.service.S3FileUploadService;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.common.dto.FileUploadInitRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class AgreementService {
    private final AgreementRepository agreementRepository;
    private final CategoryRepository categoryRepository;
    private final S3FileUploadService s3FileUploadService;
    private final RestTemplate restTemplate;

    @Value("${secret.flask.ip}")
    private String FLASK_SERVER_IP;

    public List<AgreementListResponseDto> findAgreementList() {
        return agreementRepository.findAllWithCategoryByOrderByCreatedAtDesc()
                .stream()
                .map(AgreementListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AgreementListResponseDto> findAgreementListByName(String name) {
        return agreementRepository.findWithCategoryByNameContaining(name)
                .stream()
                .map(AgreementListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AgreementListResponseDto> findAgreementListByCategoryId(Long categoryId) {
        List<Agreement> agreements = agreementRepository.findWithCategoryByCategoryId(categoryId);
        if(agreements.isEmpty()) {
            throw new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR);
        }
        return agreements
                .stream()
                .map(AgreementListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Long initFileUpload(FileUploadInitRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

        Agreement agreement = Agreement.builder()
                .name(requestDto.getName())
                .type(requestDto.getType())
                .category(category)
                .build();

        return agreementRepository.save(agreement).getId();
    }
  
    @Transactional(propagation = Propagation.NOT_SUPPORTED) // FAIL 예외 처리를 위해 NOT_SUPPORTED로 설정
    public void uploadFile(MultipartFile file, Long id) {

        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        String fileName = null;
        try {
            fileName = s3FileUploadService.uploadFile(file);
        } catch (ApplicationException e) {
            agreement.updateFileStatus(null, FileStatus.FAILED, null);
            agreementRepository.save(agreement);
            throw e; // 예외 다시 던지기
        }
        String url = "s3://" + s3FileUploadService.getBucketName() + "/" + fileName;
        agreement.updateFileStatus(url, FileStatus.SUCCESS, AiStatus.ANALYZING);
        agreementRepository.save(agreement);
    }

    public void deleteAgreement(Long id) {
        Agreement agreement = agreementRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));

        String flaskUrl = FLASK_SERVER_IP + "/flask/agreements/" + id;

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
                agreementRepository.delete(agreement);
            } else {
                throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "F-" + body.getCode(), body.getMessage());
            }
        } catch (RestClientException e) {
            throw new ApplicationException(ErrorCode.FLASK_SERVER_CONNECTION_ERROR);
        }
    }

    public void cancelFileUpload(Long id) {
        Agreement agreement = agreementRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));

        if (agreement.getFileStatus() != null || agreement.getAiStatus() != null) {
            throw new ApplicationException(ErrorCode.FILE_DELETE_ERROR);
        }
        agreementRepository.delete(agreement);
    }
}
