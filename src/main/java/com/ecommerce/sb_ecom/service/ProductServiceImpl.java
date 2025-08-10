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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ModelMapper modelMapper;

    @Autowired
     FileService fileService;

    @Value("${project.image}")
    private String path;
    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        boolean isProductNotPresent = false;
        List<Product> products = category.getProducts();
        for(int i=0;i<products.size();i++)
        {
            if(products.get(i).getProductName().equals(productDTO.getProductName()))
                isProductNotPresent = true;
            break;
        }
        if(!isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setProductImage("default.png");
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            product.setCategory(category);
            Product saveProduct = productRepository.save(product);
            return modelMapper.map(saveProduct, ProductDTO.class);
        }
        else{
            throw new APIException("Product already exits!!");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByOrder);
        Page<Product> ProductPage = productRepository.findAll(pageDetails);
        List<Product> products = ProductPage.getContent();
        if (products.isEmpty())
            throw new APIException("No products found");
        List<ProductDTO> ProductDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse response = new ProductResponse();
        response.setContent(ProductDTOs);
        response.setPageNumber(ProductPage.getNumber());
        response.setPageSize(ProductPage.getSize());
        response.setTotalElements(ProductPage.getTotalElements());
        response.setTotalPages(ProductPage.getTotalPages());
        response.setLastPage(ProductPage.isLast());
        return response;
    }

    @Override
    public ProductResponse getProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByOrder);
        Page<Product> ProductPage = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> products = ProductPage.getContent();

        if (products.isEmpty())
            throw new APIException("No products found");
        List<ProductDTO> ProductDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse response = new ProductResponse();
        response.setContent(ProductDTOs);
        response.setPageNumber(ProductPage.getNumber());
        response.setPageSize(ProductPage.getSize());
        response.setTotalElements(ProductPage.getTotalElements());
        response.setTotalPages(ProductPage.getTotalPages());
        response.setLastPage(ProductPage.isLast());
        return response;
    }

    @Override
    public ProductResponse getProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByOrder);
        Page<Product> ProductPage = productRepository.findByProductNameLikeIgnoreCase("%"+ keyword +"%",pageDetails);
        List<Product> products = ProductPage.getContent();
        System.out.println("Get product list" + products);
        System.out.println("Get keyword" + keyword);
         for(Product i: products)
             System.out.println(i);
        if (products.isEmpty())
            throw new APIException("No products found");
        List<ProductDTO> ProductDTOs = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse response = new ProductResponse();
        response.setContent(ProductDTOs);
        response.setPageNumber(ProductPage.getNumber());
        response.setPageSize(ProductPage.getSize());
        response.setTotalElements(ProductPage.getTotalElements());
        response.setTotalPages(ProductPage.getTotalPages());
        response.setLastPage(ProductPage.isLast());
        return response;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product product = modelMapper.map(productDTO, Product.class);
        Product getProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        getProduct.setProductName(product.getProductName());
        getProduct.setDescription(product.getDescription());
        getProduct.setQuantity(product.getQuantity());
        getProduct.setPrice(product.getPrice());
        getProduct.setDiscount(product.getDiscount());
        double specialPrice = getProduct.getPrice() - ((getProduct.getDiscount() * 0.01) * getProduct.getPrice());
        getProduct.setSpecialPrice(specialPrice);
        Product saveProduct = productRepository.save(getProduct);
        return modelMapper.map(saveProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product getProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        ProductDTO deletedProductDTO = modelMapper.map(getProduct, ProductDTO.class);
        productRepository.delete(getProduct);
        return deletedProductDTO;
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get the product from DB
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));
        //upload to server
        //get file name of the uploaded image

        String fileName = fileService.uploadImage(path, image);
        //updating the filename to the product
        product.setProductImage(fileName);
        //Save product
        Product updatedProduct = productRepository.save(product);
        //return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }


}
