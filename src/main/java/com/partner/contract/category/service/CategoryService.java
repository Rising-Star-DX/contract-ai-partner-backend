package com.partner.contract.category.service;

import com.partner.contract.category.domain.Category;
import com.partner.contract.category.dto.CategoryListResponseDto;
import com.partner.contract.category.repository.CategoryRepository;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
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

    public void addCategory(Category category) {
        Category existedCategory = categoryRepository.findByName(category.getName());

        if(existedCategory != null) {
            throw new ApplicationException(ErrorCode.CATEGORY_ALREADY_EXISTS_ERROR);
        }

        categoryRepository.save(category);
    }
}
