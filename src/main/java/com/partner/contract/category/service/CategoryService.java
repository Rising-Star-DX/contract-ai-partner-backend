package com.partner.contract.category.service;

import com.partner.contract.category.dto.CategoryListResponseDto;
import com.partner.contract.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryListResponseDto> findCategoryList() {
        return categoryRepository.findAllWithStandardAndAgreementByOrderByName();
    }

    public List<CategoryListResponseDto> findCategoryListByName(String name) {
        return categoryRepository.findAllWithStandardAndAgreementByNameOrderByName(name);
    }
}
