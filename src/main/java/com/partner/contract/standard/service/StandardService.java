package com.partner.contract.standard.service;

import com.partner.contract.category.domain.Category;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.service.FileConversionService;
import com.partner.contract.common.service.S3Service;
import com.partner.contract.common.utils.DocumentStatusUtil;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.domain.StandardContent;
import com.partner.contract.standard.dto.StandardContentRequestDto;
import com.partner.contract.standard.dto.StandardDetailsResponseDto;
import com.partner.contract.standard.dto.StandardDetailsResponseForAdminDto;
import com.partner.contract.standard.dto.StandardListResponseDto;
import com.partner.contract.standard.repository.StandardContentRepository;
import com.partner.contract.standard.repository.StandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StandardService {
    private final StandardRepository standardRepository;
    private final StandardContentRepository standardContentRepository;
    private final StandardAnalysisAsyncService standardAnalysisAsyncService;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;
    private final S3Service s3Service;
    private final FileConversionService fileConversionService;

    @Value("${secret.flask.ip}")
    private String FLASK_SERVER_IP;

    public List<StandardListResponseDto> findStandardList(String name, Long categoryId) {
        List<Standard> standards;

        if(categoryId == null) {
            standards = standardRepository.findWithCategoryByNameContainingOrderByCreatedAtDesc(name);
        }
        else {
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

            standards = standardRepository.findStandardListOrderByCreatedAtDesc(name, categoryId);
        }

        return standards
                .stream()
                .map(StandardListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public StandardDetailsResponseDto findStandardById(Long id) {
        Standard standard = standardRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));
        return StandardDetailsResponseDto.fromEntity(standard);
    }

    public StandardDetailsResponseForAdminDto findStandardByIdForAdmin(Long id) {
        Standard standard = standardRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        return StandardDetailsResponseForAdminDto.builder()
                .id(standard.getId())
                .name(standard.getName())
                .type(standard.getType())
                .url(standard.getUrl())
                .status(DocumentStatusUtil.determineStatus(standard.getFileStatus(), standard.getAiStatus()))
                .createdAt(standard.getCreatedAt())
                .categoryName(standard.getCategory().getName())
                .standardContentResponseDtoList(standardRepository.findstandardContentResponseByStandardId(standard.getId()))
                .build();
    }

    public void deleteStandard(Long id) {
        Standard standard = standardRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        if(standard.getFileStatus() == FileStatus.UPLOADING || standard.getAiStatus() == AiStatus.ANALYZING) {
            throw new ApplicationException(ErrorCode.FILE_DELETE_ERROR);
        }

        String flaskUrl = FLASK_SERVER_IP + "/flask/standards/" + standard.getCategory().getName() + "/" + id;

        FlaskResponseDto<String> body;

        try {
            // Flask에 API 요청
            ResponseEntity<FlaskResponseDto<String>> response = restTemplate.exchange(
                    flaskUrl,
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<FlaskResponseDto<String>>() {} // ✅ 제네릭 타입 유지
            );

            body = response.getBody();

        } catch (RestClientException e) {
            throw new ApplicationException(ErrorCode.FLASK_SERVER_CONNECTION_ERROR, e.getMessage());
        }

        try {
            if ("success".equals(body.getData())) { // 기준문서 분석 성공
                s3Service.deleteFile(standard.getUrl());
                standardRepository.delete(standard);
            } else {
                throw new ApplicationException(ErrorCode.FLASK_DELETE_ERROR);
            }
        } catch (NullPointerException e) {
            throw new ApplicationException(ErrorCode.FLASK_RESPONSE_NULL_ERROR, e.getMessage());
        }
    }

    public Long uploadFile(MultipartFile file, Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

        Standard standard = Standard.builder()
                .name(file.getOriginalFilename())
                .type(FileType.fromContentType(file.getContentType()))
                .category(category)
                .build();

        if(standard.getType().isConvertiblePdf()) {
            file = fileConversionService.convertFileToPdf(file, standard.getType());
        }

        // S3 파일 저장
        String url = null;
        try {
            url = s3Service.uploadFile(file, "standards");
        } catch (ApplicationException e) {
            throw e; // 예외 다시 던지기
        }
        standard.updateFileStatus(url, FileStatus.SUCCESS);
        return standardRepository.save(standard).getId();
    }

    @Async
    @Transactional
    public void startAnalyze(Long id) {
        Standard standard = standardRepository.findWithCategoryById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        if (standard.getFileStatus() != FileStatus.SUCCESS) {
            throw new ApplicationException(ErrorCode.MISSING_FILE_FOR_ANALYSIS);
        } else if (standard.getAiStatus() == AiStatus.FAILED || standard.getAiStatus() == AiStatus.SUCCESS) { // 이미 분석이 완료된 경우
            throw new ApplicationException(ErrorCode.AI_ANALYSIS_ALREADY_COMPLETED);
        } else if (standard.getAiStatus() == AiStatus.ANALYZING) { // 이미 분석 중인 경우
            throw new ApplicationException(ErrorCode.AI_ANALYSIS_ALREADY_STARTED);
        }

        standard.updateAiStatus(AiStatus.ANALYZING);
        standardRepository.save(standard);

        // 비동기 분석 요청
        standardAnalysisAsyncService.analyze(standard, standard.getCategory().getName());
    }

    public void cancelFileUpload(Long id) {
        Standard standard = standardRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        if (standard.getFileStatus() == FileStatus.SUCCESS && standard.getAiStatus() != AiStatus.ANALYZING){
            // S3에 업로드 된 파일 삭제
            s3Service.deleteFile(standard.getUrl());

            // RDB 데이터 삭제
            standardRepository.delete(standard);
        } else {
            throw new ApplicationException(ErrorCode.FILE_DELETE_ERROR);
        }
    }

    public Boolean checkAnalysisCompleted(Long id) {
        Standard standard = standardRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));

        if (standard.getAiStatus() == null){
            throw new ApplicationException(ErrorCode.AI_ANALYSIS_NOT_STARTED);
        }

        return standard.getAiStatus() != AiStatus.ANALYZING;
    }

    public void updateExpiredAnalysisStatus() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Standard> standards = standardRepository.findByAiStatusAndCreatedAtBefore(AiStatus.ANALYZING, fiveMinutesAgo);

        for(Standard standard : standards) {
            standard.updateAiStatus(AiStatus.FAILED);
        }
        standardRepository.saveAll(standards);
    }

    public void modifyStandard(Long id, List<StandardContentRequestDto> contents) {
        Standard standard = standardRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.STANDARD_NOT_FOUND_ERROR));
        for(StandardContentRequestDto content : contents) {
            StandardContent standardContent = standardContentRepository.findById(content.getId())
                    .orElseThrow(() -> new ApplicationException(ErrorCode.STANDARDCONTENT_NOT_FOUND_ERROR));

            standardContent.updateContent(content.getContent());
            standardContentRepository.save(standardContent);
        }

        // 1. txt 파일로 변환
        List<String> contentList = standard.getStandardContentList()
                .stream()
                .map(StandardContent::getContent)
                .collect(Collectors.toList());

        MultipartFile contentsTxt = fileConversionService.convertStringToTxt(standard.getName().substring(0, standard.getName().lastIndexOf(".")), contentList);

        // 2. pdf 변환
        MultipartFile file = fileConversionService.convertFileToPdf(contentsTxt, FileType.TXT);

        // 3. s3 기존 파일 지우고, 업로드
        String url = null;
        try {
            s3Service.deleteFile(standard.getUrl());
            url = s3Service.uploadFile(file, "standards");
        } catch (ApplicationException e) {
            throw e; // 예외 다시 던지기
        }

        // 4. url 및 상태 업데이트
        standard.updateFileStatus(url, FileStatus.SUCCESS);
        standard.updateAiStatus(null);
        standardRepository.save(standard).getId();

        // 5. ai 분석 요청
        startAnalyze(standard.getId());
    }
}
