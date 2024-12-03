package com.ecommerce.project.service;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new MyResourceNotFoundException("Category", "categoryId", categoryId));

        Product product = modelMapper.map(productDTO, Product.class);
        product.setCategory(category);
        product.setImage("default.png");
        product.setSpecialPrice(product.getPrice()-(product.getDiscount()*product.getPrice()*0.01));

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> allProducts = productRepository.findAll();
        List<ProductDTO> productDTOS = allProducts.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new MyResourceNotFoundException("Category", "categoryId", categoryId));
        List<Product> allProducts = productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS = allProducts.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductResponse getProductByKeyword(String keyword) {

        List<Product> allProducts = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%');
        List<ProductDTO> productDTOS = allProducts.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
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
        String path = "images/";
        String fileName = uploadImage(path, image);

        //update the new file name in the product object
        product.setImage(fileName);

        //save updated product
        Product updatedProduct = productRepository.save(product);

        //return updated product

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    private String uploadImage(String path, MultipartFile file) throws IOException {

        //File names of the current / original file.
        String originalFilename = file.getOriginalFilename();

        //Generate a unique file name
        String randomUUID = UUID.randomUUID().toString();
        assert originalFilename != null;
        String fileName = randomUUID.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
        String filePath = path + File.separator + fileName;

        //Check if path exists or not. If not, create a new directory.
        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdir();
        }

        //Upload the file to the server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        //return the file name of the uploaded file
        return fileName;
    }
}
