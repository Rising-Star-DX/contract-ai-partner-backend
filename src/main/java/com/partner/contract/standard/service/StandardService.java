package com.partner.contract.standard.service;

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

    public List<StandardResponseDto> findStandardList() {
        return standardRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(StandardResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<StandardResponseDto> findStandardListByName(String name) {
        return standardRepository.findByNameContaining(name)
                .stream()
                .map(StandardResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<StandardResponseDto> findStandardListByCategoryId(Long categoryId) {
        return standardRepository.findByCategoryId(categoryId)
                .stream()
                .map(StandardResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
