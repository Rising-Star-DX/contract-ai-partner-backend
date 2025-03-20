package com.partner.contract.agreement.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.repository.AgreementRepository;
import com.partner.contract.category.domain.Category;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.common.dto.FileUploadInitRequestDto;
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.service.S3FileUploadService;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.standard.domain.Standard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
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
  
    public Long uploadFile(MultipartFile file, Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

        Agreement agreement = Agreement.builder()
                .name(file.getOriginalFilename())
                .type(FileType.fromContentType(file.getContentType()))
                .category(category)
                .build();

        // s3 파일 저장
        String fileName = null;
        try {
            fileName = s3FileUploadService.uploadFile(file, "agreements");
        } catch (ApplicationException e) {
            throw e; // 예외 다시 던지기
        }
        String url = "s3://" + s3FileUploadService.getBucketName() + "/" + fileName;
        agreement.updateFileStatus(url, FileStatus.SUCCESS, AiStatus.ANALYZING);
        return agreementRepository.save(agreement).getId();
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

            if (body != null && body.getData() != null && "success".equals(body.getData())) {
                agreementRepository.delete(agreement);
            } else {
                throw new ApplicationException(ErrorCode.FLASK_SERVER_ERROR, "fail");
            }
        } catch (RestClientException e) {
            throw new ApplicationException(ErrorCode.FLASK_SERVER_CONNECTION_ERROR, e.getMessage());
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
