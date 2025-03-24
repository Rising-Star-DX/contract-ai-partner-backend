package com.partner.contract.agreement.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.AgreementDetailsResponseDto;
import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.repository.AgreementRepository;
import com.partner.contract.category.domain.Category;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.service.S3Service;
import com.partner.contract.common.utils.DocumentStatusUtil;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
    private final S3Service s3Service;
    private final RestTemplate restTemplate;

    @Value("${secret.flask.ip}")
    private String FLASK_SERVER_IP;

    public List<AgreementListResponseDto> findAgreementList(String name, Long categoryId) {
        List<Agreement> agreements;

        if(categoryId == null) {
            agreements = agreementRepository.findWithCategoryByNameContainingOrderByCreatedAtDesc(name);
        }
        else {
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

            agreements = agreementRepository.findAgreementListOrderByCreatedAtDesc(name, categoryId);
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
            fileName = s3Service.uploadFile(file, "agreements");
        } catch (ApplicationException e) {
            throw e; // 예외 다시 던지기
        }
        String url = "s3://" + s3Service.getBucketName() + "/" + fileName;
        agreement.updateFileStatus(url, FileStatus.SUCCESS);
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

        if (agreement.getFileStatus() == FileStatus.SUCCESS && agreement.getAiStatus() != AiStatus.ANALYZING){
            // S3에 업로드 된 파일 삭제
            s3Service.deleteFile(agreement.getUrl());

            // RDB 데이터 삭제
            agreementRepository.delete(agreement);
        } else {
            throw new ApplicationException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    public AgreementDetailsResponseDto findAgreementDetailsById(Long id) {
        Agreement agreement = agreementRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));
        AgreementDetailsResponseDto agreementDetailsResponseDto = AgreementDetailsResponseDto.builder()
                .id(agreement.getId())
                .name(agreement.getName())
                .type(agreement.getType())
                .url(agreement.getUrl())
                .status(DocumentStatusUtil.determineStatus(agreement.getFileStatus(), agreement.getAiStatus()))
                .categoryName(agreement.getCategory().getName())
                .incorrectTextResponseDtoList(agreementRepository.findIncorrectTextByAgreementId(id))
                .build();

        return agreementDetailsResponseDto;
    }
  
    public Boolean checkAnalysisCompleted(Long id) {
        Agreement agreement = agreementRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));

        if (agreement.getAiStatus() == null){
            throw new ApplicationException(ErrorCode.AI_ANALYSIS_NOT_STARTED);
        }

        return agreement.getAiStatus() != AiStatus.ANALYZING;
    }
}
