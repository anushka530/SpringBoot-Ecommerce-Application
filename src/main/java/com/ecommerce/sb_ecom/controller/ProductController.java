package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.config.AppContants;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody  ProductDTO productDTO, @PathVariable Long categoryId){
        ProductDTO savedProductDTO = productService.addProduct(categoryId,productDTO);
        return new ResponseEntity<>(savedProductDTO,HttpStatus.CREATED);
    }
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(value = "pageNumber",defaultValue = AppContants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(value = "pageSize",defaultValue = AppContants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(value = "sortBy",defaultValue = AppContants.SORT_PRODUCT_BY,required = false) String sortBy,
            @RequestParam(value = "sortOrder",defaultValue = AppContants.SORT_DIR,required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategoryId(@PathVariable  Long categoryId,
                                                                   @RequestParam(value = "pageNumber",defaultValue = AppContants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                   @RequestParam(value = "pageSize",defaultValue = AppContants.PAGE_SIZE,required = false) Integer pageSize,
                                                                   @RequestParam(value = "sortBy",defaultValue = AppContants.SORT_PRODUCT_BY,required = false) String sortBy,
                                                                   @RequestParam(value = "sortOrder",defaultValue = AppContants.SORT_DIR,required = false) String sortOrder){
        ProductResponse productResponse = productService.getProductsByCategoryId(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(value = "pageNumber",defaultValue = AppContants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                @RequestParam(value = "pageSize",defaultValue = AppContants.PAGE_SIZE,required = false) Integer pageSize,
                                                                @RequestParam(value = "sortBy",defaultValue = AppContants.SORT_PRODUCT_BY,required = false) String sortBy,
                                                                @RequestParam(value = "sortOrder",defaultValue = AppContants.SORT_DIR,required = false) String sortOrder)
    {
        ProductResponse productResponse = productService.getProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }
    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId)
    {
        ProductDTO getProductDTO = productService.updateProduct(productDTO,productId);
        return new ResponseEntity<>(getProductDTO,HttpStatus.OK);
    }
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId)
    {
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO productDTO = productService.updateProductImage(productId,image);
        return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }
}
