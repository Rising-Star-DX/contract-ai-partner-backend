package com.partner.contract.category.controller;

import com.partner.contract.category.dto.CategoryListResponseDto;
import com.partner.contract.category.service.CategoryService;
import com.partner.contract.global.exception.dto.SuccessResponse;
import com.partner.contract.global.exception.error.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<CategoryListResponseDto>>> categoryList(@RequestParam(name = "name", required = false, defaultValue = "") String name) {
        List<CategoryListResponseDto> categories = categoryService.findCategoryList(name);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<Map<String, Boolean>>> categoryStandardCheck(@PathVariable("id") Long id) {
        Boolean existence = categoryService.checkStandardExistence(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SELECT_SUCCESS.getCode(), SuccessCode.SELECT_SUCCESS.getMessage(), Map.of("result", existence)));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<String>> categoryAdd(@RequestBody String categoryName) {
        categoryService.addCategory(categoryName);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.INSERT_SUCCESS.getCode(), SuccessCode.INSERT_SUCCESS.getMessage(), null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> categoryModify(@PathVariable("id") Long id, @RequestBody String categoryName) {
        categoryService.modifyCategory(id, categoryName);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.UPDATE_SUCCESS.getCode(), SuccessCode.UPDATE_SUCCESS.getMessage(), null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> categoryDelete(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);

        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.DELETE_SUCCESS.getCode(), SuccessCode.DELETE_SUCCESS.getMessage(), null));
    }
}
