package com.partner.contract.category.service;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.service.AgreementService;
import com.partner.contract.category.domain.Category;
import com.partner.contract.category.dto.CategoryListResponseDto;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.service.StandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryListResponseDto> findCategoryList(String name) {
        return categoryRepository.findCategoryListOrderByName(name);
    }

    public Boolean checkStandardExistence(Long id) {
        categoryRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

        return categoryRepository.findWithStandardById(id) > 0;
    }

    public void addCategory(String categoryName) {
        Category existedCategory = categoryRepository.findByName(categoryName);

        if(existedCategory != null) {
            throw new ApplicationException(ErrorCode.CATEGORY_ALREADY_EXISTS_ERROR);
        }

        Category category = Category.builder()
                .name(categoryName)
                .build();

        categoryRepository.save(category);
    }


    public void modifyCategory(Long id, String categoryName) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

        Category existedCategory = categoryRepository.findByName(categoryName);

        if(existedCategory != null) {
            throw new ApplicationException(ErrorCode.CATEGORY_ALREADY_EXISTS_ERROR);
        }

        category.update(categoryName);
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ApplicationException(ErrorCode.CATEGORY_NOT_FOUND_ERROR));

        List<Standard> standards = category.getStandardList();
        List<Agreement> agreements = category.getAgreementList();

        if(!standards.isEmpty() || !agreements.isEmpty()) {
            throw new ApplicationException(ErrorCode.CATEGORY_DOCUMENT_ALREADY_EXISTS_ERROR);
        }

        categoryRepository.deleteById(id);
    }
}
