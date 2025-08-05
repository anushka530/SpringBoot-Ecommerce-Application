package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.Repository.CategoryRepository;
import com.ecommerce.sb_ecom.Repository.ProductRepository;
import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public ProductDTO addProduct(Long categoryId, Product product) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","CategoryId",categoryId));
        product.setProductImage("default.png");
        double specialPrice = product.getPrice() - ((product.getDiscount()*0.01 )*product.getPrice());
        product.setSpecialPrice(specialPrice);
        product.setCategory(category);
        Product saveProduct = productRepository.save(product);
        return modelMapper.map(saveProduct,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty())
            throw new APIException("No products found");
        List<ProductDTO> ProductDTOs = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse response = new ProductResponse();
        response.setContent(ProductDTOs);
        return response;
    }

    @Override
    public ProductResponse getProductsByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","CategoryId",categoryId));
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        if(products.isEmpty())
            throw new APIException("No products found");
        List<ProductDTO> ProductDTOs = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse response = new ProductResponse();
        response.setContent(ProductDTOs);
        return response;
    }

    @Override
    public ProductResponse getProductByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%');
        if(products.isEmpty())
            throw new APIException("No products found");
        List<ProductDTO> ProductDTOs = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse response = new ProductResponse();
        response.setContent(ProductDTOs);
        return response;
    }
}
