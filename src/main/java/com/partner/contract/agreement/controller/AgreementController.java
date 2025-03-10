package com.partner.contract.agreement.controller;

import com.partner.contract.agreement.service.AgreementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/agreement")
public class AgreementController {
    private final AgreementService agreementService;
}
