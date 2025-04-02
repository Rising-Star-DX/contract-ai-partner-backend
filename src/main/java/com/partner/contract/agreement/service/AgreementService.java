package com.partner.contract.agreement.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.AgreementDetailsResponseDto;
import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.repository.AgreementRepository;
import com.partner.contract.category.domain.Category;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.service.FileConversionService;
import com.partner.contract.common.service.S3Service;
import com.partner.contract.common.utils.DocumentStatusUtil;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.standard.repository.StandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AgreementService {
    private final AgreementAnalysisAsyncService agreementAnalysisAsyncService;
    private final AgreementRepository agreementRepository;
    private final StandardRepository standardRepository;
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;
    private final FileConversionService fileConversionService;

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

        if(agreement.getType() == FileType.DOC || agreement.getType() == FileType.DOCX) {
            file = fileConversionService.convertFileToPdf(file, agreement.getType());
        }
        // s3 파일 저장
        String fileName = null;
        try {
            fileName = s3Service.uploadFile(file, "agreements");
        } catch (ApplicationException e) {
            throw e; // 예외 다시 던지기
        }

        agreement.updateFileStatus(fileName, FileStatus.SUCCESS);
        return agreementRepository.save(agreement).getId();
    }

    public void deleteAgreement(Long id) {
        Agreement agreement = agreementRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));

        if(agreement.getFileStatus() == FileStatus.UPLOADING || agreement.getAiStatus() == AiStatus.ANALYZING) {
            throw new ApplicationException(ErrorCode.FILE_DELETE_ERROR);
        }

        s3Service.deleteFile(agreement.getUrl());
        agreementRepository.delete(agreement);
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
                .totalPage(agreement.getTotalPage())
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

    public void updateExpiredAnalysisStatus() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Agreement> agreements = agreementRepository.findByAiStatusAndCreatedAtBefore(AiStatus.ANALYZING, fiveMinutesAgo);

        for(Agreement agreement : agreements) {
            agreement.updateAiStatus(AiStatus.FAILED);
        }
        agreementRepository.saveAll(agreements);
    }

    @Transactional(noRollbackFor = ApplicationException.class)
    public void startAnalyze(Long id) {
        Agreement agreement = agreementRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));

        Boolean fileExists = standardRepository.existsByCategoryIdAndAiStatus(agreement.getCategory().getId(), AiStatus.SUCCESS);
        if (!fileExists) { // 학습된 파일이 존재하지 않는 경우
            agreement.updateAiStatus(AiStatus.FAILED);
            agreementRepository.save(agreement);
            throw new ApplicationException(ErrorCode.NO_ANALYSIS_STANDARD_DOCUMENT);
        }

        if (agreement.getFileStatus() != FileStatus.SUCCESS) {
            agreement.updateAiStatus(AiStatus.FAILED);
            agreementRepository.save(agreement);
            throw new ApplicationException(ErrorCode.MISSING_FILE_FOR_ANALYSIS);
        } else if (agreement.getAiStatus() == AiStatus.FAILED || agreement.getAiStatus() == AiStatus.SUCCESS) {
            agreement.updateAiStatus(AiStatus.FAILED);
            agreementRepository.save(agreement);
            throw new ApplicationException(ErrorCode.AI_ANALYSIS_ALREADY_COMPLETED);
        } else if (agreement.getAiStatus() == AiStatus.ANALYZING) {
            agreement.updateAiStatus(AiStatus.FAILED);
            agreementRepository.save(agreement);
            throw new ApplicationException(ErrorCode.AI_ANALYSIS_ALREADY_STARTED);
        }

        agreement.updateAiStatus(AiStatus.ANALYZING);
        agreementRepository.save(agreement);

        // 비동기로 분석 요청
        agreementAnalysisAsyncService.analyze(agreement, agreement.getCategory().getName());
    }
}
