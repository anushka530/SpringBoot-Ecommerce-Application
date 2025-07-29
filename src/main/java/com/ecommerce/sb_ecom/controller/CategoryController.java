package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.config.AppContants;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api")
public class CategoryController {
    private CategoryService categoryservice;
    public CategoryController(CategoryService categoryservice) {
        this.categoryservice = categoryservice;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name="pageNumber",defaultValue = AppContants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name="pageSize",defaultValue = AppContants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name="sortBy",defaultValue = AppContants.SORT_CATEGORY_BY,required = false) String sortBy,
            @RequestParam(name="sortOrder",defaultValue = AppContants.SORT_DIR,required = false) String sortOrder
    )
    {
        CategoryResponse categoryResponse = categoryservice.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO)
    {
        CategoryDTO savedCategoryDTO = categoryservice.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId)
    {
        CategoryDTO deletedCategory = categoryservice.deleteCategory(categoryId);
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody  CategoryDTO categoryDTO , @PathVariable Long categoryId){

            CategoryDTO updateCategoryDTO = categoryservice.updateCategory(categoryDTO,categoryId);
            return new ResponseEntity<>(updateCategoryDTO,HttpStatus.OK);

    }
}
