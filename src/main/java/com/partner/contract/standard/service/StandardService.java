package com.partner.contract.standard.service;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.dto.StandardResponseDto;
import com.partner.contract.standard.repository.StandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StandardService {
    private final StandardRepository standardRepository;

    public List<StandardResponseDto> getAllStandards() {
        List<Standard> standards = standardRepository.findAll();
        if(standards.isEmpty()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_ERROR);
        }
        return standards.stream()
                .map(StandardResponseDto::fromEntity).collect(Collectors.toList());
    }
}
