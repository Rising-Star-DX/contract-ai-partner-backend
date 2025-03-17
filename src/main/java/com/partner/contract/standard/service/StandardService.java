package com.partner.contract.standard.service;

import com.partner.contract.category.domain.Category;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.common.dto.FlaskResponseDto;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.dto.FileUploadInitRequestDto;
import com.partner.contract.standard.dto.StandardListResponseDto;
import com.partner.contract.standard.dto.StandardResponseDto;
import com.partner.contract.standard.repository.StandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StandardService {
    private final StandardRepository standardRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;

    @Value("${secret.flask.ip}")
    private String FLASK_SERVER_IP;

    public List<StandardListResponseDto> findStandardList() {
        return standardRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(StandardListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<StandardListResponseDto> findStandardListByName(String name) {
        return standardRepository.findByNameContaining(name)
                .stream()
                .map(StandardListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<StandardListResponseDto> findStandardListByCategoryId(Long categoryId) {
        List<Standard> standards = standardRepository.findByCategoryId(categoryId);
        if(standards.isEmpty()) {
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

        String flaskUrl = "http://" + FLASK_SERVER_IP + "/flask/standard/" + id;
        ResponseEntity<FlaskResponseDto> response = restTemplate.exchange(flaskUrl, HttpMethod.DELETE, null, FlaskResponseDto.class);
        FlaskResponseDto flaskResponseBody = response.getBody();

        if(flaskResponseBody == null) {
            throw new ApplicationException(ErrorCode.Flask_SERVER_ERROR);
        }

        if ("success".equals(flaskResponseBody.getData())) {
            standardRepository.delete(standard);
        } else {
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, flaskResponseBody.getCode(), flaskResponseBody.getMessage());
        }
    }
}
