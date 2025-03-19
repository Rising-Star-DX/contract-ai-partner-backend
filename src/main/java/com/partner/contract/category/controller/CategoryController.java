package com.partner.contract.category.controller;

import com.partner.contract.category.dto.CategoryListResponseDto;
import com.partner.contract.category.service.CategoryService;
import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<CategoryListResponseDto>>> categoryList() {
        List<CategoryListResponseDto> categories = categoryService.findCategoryList();

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), categories));
    }
}
