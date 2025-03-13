package com.partner.contract.agreement.service;

import com.partner.contract.agreement.dto.AgreementResponseDto;
import com.partner.contract.agreement.repository.AgreementRepository;
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

    public List<AgreementResponseDto> findAgreementList() {
        return agreementRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AgreementResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AgreementResponseDto> findAgreementListByName(String name) {
        return agreementRepository.findByNameContaining(name)
                .stream()
                .map(AgreementResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AgreementResponseDto> findAgreementListByCategoryId(Long categoryId) {
        return agreementRepository.findByCategoryId(categoryId)
                .stream()
                .map(AgreementResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
