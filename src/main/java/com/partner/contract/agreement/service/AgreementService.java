package com.partner.contract.agreement.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.domain.AgreementIncorrectText;
import com.partner.contract.agreement.dto.AgreementAnalysisFlaskResponseDto;
import com.partner.contract.agreement.dto.AgreementDetailsResponseDto;
import com.partner.contract.agreement.dto.AgreementIncorrectTextDto;
import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.repository.AgreementIncorrectTextRepository;
import com.partner.contract.agreement.repository.AgreementRepository;
import com.partner.contract.category.domain.Category;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.common.dto.AnalysisRequestDto;
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
public class AgreementService {
    private final AgreementRepository agreementRepository;
    private final AgreementIncorrectTextRepository agreementIncorrectTextRepository;
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
        String url = "https://" + s3Service.getBucketName() + "/" + fileName;
        agreement.updateFileStatus(url, FileStatus.SUCCESS);
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
                .totalPage(10) // 추후에 바꿔줘야 함.
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

//    public void analyze(Long id) {
//        Agreement agreement = agreementRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));
//
//        if (agreement.getFileStatus() != FileStatus.SUCCESS) {
//            throw new ApplicationException(ErrorCode.AI_ANALYSIS_ALREADY_COMPLETED);
//        } else if (agreement.getAiStatus() == AiStatus.FAILED || agreement.getAiStatus() == AiStatus.SUCCESS) {
//            throw new ApplicationException(ErrorCode.AI_ANALYSIS_ALREADY_COMPLETED);
//        }
//
//        // 프론트엔드를 위한 가짜 AI 분석 결과 저장
//        AgreementIncorrectText textInfo = AgreementIncorrectText.builder()
//                .accuracy(57.7)
//                .incorrectText("근로조건은 근로자와 사용자가 동등한 지위에서 자유의사에 따라 결정")
//                .agreement(agreement)
//                .page(1)
//                .proofText("위배 문구가 되는 근거입니다.")
//                .correctedText("이와 같이 수정하시면 됩니다.")
//                .build();
//        agreementIncorrectTextRepository.save(textInfo);
//
//        AgreementIncorrectText textInfo2 = AgreementIncorrectText.builder()
//                .accuracy(83.1)
//                .incorrectText("폭행, 협박, 감금, 그 밖에 정신상 또는 신체상의 자유를 부당하게 구속하는 수단")
//                .agreement(agreement)
//                .page(1)
//                .proofText("위배 문구가 되는 근거입니다.")
//                .correctedText("이와 같이 수정하시면 됩니다.")
//                .build();
//
//        agreementIncorrectTextRepository.save(textInfo2);
//
//        // delay
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        agreement.updateAiStatus(AiStatus.SUCCESS);
//    }

    public void updateExpiredAnalysisStatus() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Agreement> agreements = agreementRepository.findByAiStatusAndCreatedAtBefore(AiStatus.ANALYZING, fiveMinutesAgo);

        for(Agreement agreement : agreements) {
            agreement.updateAiStatus(AiStatus.FAILED);
        }
        agreementRepository.saveAll(agreements);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED) // FAIL 예외 처리를 위해 NOT_SUPPORTED로 설정
    public void analyze(Long id) {
        Agreement agreement = agreementRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.AGREEMENT_NOT_FOUND_ERROR));

        if (agreement.getFileStatus() != FileStatus.SUCCESS) {
            throw new ApplicationException(ErrorCode.MISSING_FILE_FOR_ANALYSIS);
        } else if (agreement.getAiStatus() == AiStatus.FAILED || agreement.getAiStatus() == AiStatus.SUCCESS) {
            throw new ApplicationException(ErrorCode.AI_ANALYSIS_ALREADY_COMPLETED);
        }

        agreement.updateAiStatus(AiStatus.ANALYZING);
        agreementRepository.save(agreement);

        // Flask에 AI 분석 요청
//        String url = FLASK_SERVER_IP + "/flask/agreements/analysis";
        String url = "http://rising-star-alb-885642517.ap-northeast-2.elb.amazonaws.com:5000/flask/agreements/analysis";

        AnalysisRequestDto analysisRequestDto = AnalysisRequestDto.builder()
                .id(agreement.getId())
                .url(agreement.getUrl())
                .categoryName(agreement.getCategory().getName())
                .type(agreement.getType())
                .build();

        // HTTP Request Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP Request Body 설정
        HttpEntity<AnalysisRequestDto> requestEntity = new HttpEntity<>(analysisRequestDto, headers);

        FlaskResponseDto<AgreementAnalysisFlaskResponseDto> body;
        try {
            // Flask에 API 요청
            ResponseEntity<FlaskResponseDto<AgreementAnalysisFlaskResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<FlaskResponseDto<AgreementAnalysisFlaskResponseDto>>() {} // ✅ 제네릭 타입 유지
            );

            body = response.getBody();

        } catch (RestClientException e) {
            agreement.updateAiStatus(AiStatus.FAILED);
            agreementRepository.save(agreement);
            throw new ApplicationException(ErrorCode.FLASK_SERVER_CONNECTION_ERROR, e.getMessage());
        }

        // Flask에서 넘어온 계약서 정보 data
        AgreementAnalysisFlaskResponseDto flaskResponseDto = body.getData();
        List<AgreementIncorrectTextDto> agreementIncorrectTextDtos = flaskResponseDto.getAgreementIncorrectTextDtos();

        // DTO -> Entity 변환 후
        List<AgreementIncorrectText> incorrectTextEntities = agreementIncorrectTextDtos.stream()
                .map(dto -> AgreementIncorrectText.builder()
                        .position(dto.getPosition())
                        .page(dto.getPage())
                        .accuracy(dto.getAccuracy())
                        .incorrectText(dto.getIncorrectText())
                        .proofText(dto.getProofText())
                        .correctedText(dto.getCorrectedText())
                        .agreement(agreement)
                        .build())
                .collect(Collectors.toList());

        agreementIncorrectTextRepository.saveAll(incorrectTextEntities);

        // AI 상태 및 분석 정보 업데이트
        agreement.updateAiStatus(AiStatus.SUCCESS);
        agreement.updateAnalysisInfomation(flaskResponseDto.getTotalPage(), flaskResponseDto.getSummaryContent());

        agreementRepository.save(agreement);
    }
}
