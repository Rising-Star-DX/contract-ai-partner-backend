package com.partner.contract.standard.controller;

import com.partner.contract.standard.service.StandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/standard")
public class StandardController {
    private final StandardService standardService;
}
