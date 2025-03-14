package com.partner.contract.agreement.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.AgreementListResponseDto;
import com.partner.contract.agreement.repository.AgreementRepository;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AgreementService {
    private final AgreementRepository agreementRepository;

    public List<AgreementListResponseDto> findAgreementList() {
        return agreementRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AgreementListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AgreementListResponseDto> findAgreementListByName(String name) {
        return agreementRepository.findByNameContaining(name)
                .stream()
                .map(AgreementListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AgreementListResponseDto> findAgreementListByCategoryId(Long categoryId) {
        List<Agreement> agreements = agreementRepository.findByCategoryId(categoryId);
        if(agreements.isEmpty()) {
            throw new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR);
        }
        return agreements
                .stream()
                .map(AgreementListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
