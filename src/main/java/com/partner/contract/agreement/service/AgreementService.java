package com.partner.contract.agreement.service;

import com.partner.contract.agreement.repository.AgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AgreementService {
    private final AgreementRepository agreementRepository;
}
