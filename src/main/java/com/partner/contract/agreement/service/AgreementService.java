package com.partner.contract.agreement.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.AgreementResponseDto;
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

    public List<AgreementResponseDto> getAllAgreements() {
        List<Agreement> agreements = agreementRepository.findAll();
        if(agreements.isEmpty()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_ERROR);
        }
        return agreements.stream()
                .map(AgreementResponseDto::fromEntity).collect(Collectors.toList());
    }
}
