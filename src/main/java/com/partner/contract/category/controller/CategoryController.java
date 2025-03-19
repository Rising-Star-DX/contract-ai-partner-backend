package com.partner.contract.category.controller;

import com.partner.contract.category.dto.CategoryListResponseDto;
import com.partner.contract.category.service.CategoryService;
import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(params = "name")
    public ResponseEntity<SuccessResponse<List<CategoryListResponseDto>>> categoryListByName(@RequestParam("name") String name) {
        List<CategoryListResponseDto> categories = categoryService.findCategoryListByName(name);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<Boolean>> categoryDetails(@PathVariable("id") Long id) {
        Boolean existence = categoryService.checkStandardExistence(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), existence));
    }
}
