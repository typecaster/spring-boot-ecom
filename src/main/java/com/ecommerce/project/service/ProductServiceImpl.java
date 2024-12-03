package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.MyResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class ProductServiceImpl implements ProductService{
    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final CategoryRepository categoryRepository;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new MyResourceNotFoundException("Category", "categoryId", categoryId));

        if (category.getProducts().stream()
                .noneMatch(product -> product.getProductName().equals(productDTO.getProductName()))) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            product.setImage("default.png");
            product.setSpecialPrice(product.getPrice() - (product.getDiscount() * product.getPrice() * 0.01));

            return modelMapper.map(productRepository.save(product), ProductDTO.class);
        } else {
            throw new RuntimeException("Product with name " + productDTO.getProductName() + " already exists");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<Product> allProducts = productPage.getContent();

        if (allProducts.isEmpty()) {
            throw new ApiException("No products found");
        }

        return getProductResponseDTOs(productPage, allProducts);
    }

    @Override
    public ProductResponse getAllProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new MyResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> allProducts = productPage.getContent();

        if (allProducts.isEmpty()) {
            throw new ApiException("No products found for this category");
        }

        return getProductResponseDTOs(productPage, allProducts);
    }

    private ProductResponse getProductResponseDTOs(Page<Product> productPage, List<Product> allProducts) {
        List<ProductDTO> productDTOS = allProducts.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> allProducts = productPage.getContent();

        if (allProducts.isEmpty()) {
            throw new ApiException("No products found for this keyword");
        }

        return getProductResponseDTOs(productPage, allProducts);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product updatedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new MyResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);
        updatedProduct.setProductName(product.getProductName());
        updatedProduct.setDescription(product.getDescription());
        updatedProduct.setQuantity(product.getQuantity());
        updatedProduct.setPrice(product.getPrice());
        updatedProduct.setDiscount(product.getDiscount());
        updatedProduct.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(updatedProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MyResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(product);

        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get the product from DB
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new MyResourceNotFoundException("Product", "productId", productId));

        //Upload image to server and get the file name of uploaded image
        String fileName = fileService.uploadImage(path, image);

        //update the new file name in the product object
        product.setImage(fileName);

        //save updated product
        Product updatedProduct = productRepository.save(product);

        //return updated product
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

}
