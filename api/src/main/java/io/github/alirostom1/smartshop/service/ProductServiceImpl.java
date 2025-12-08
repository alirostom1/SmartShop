package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.product.CreateProductRequest;
import io.github.alirostom1.smartshop.dto.request.product.FilterProductsRequest;
import io.github.alirostom1.smartshop.dto.request.product.UpdateProductRequest;
import io.github.alirostom1.smartshop.dto.response.product.ProductInternalResponse;
import io.github.alirostom1.smartshop.dto.response.product.ProductPublicResponse;
import io.github.alirostom1.smartshop.exception.ProductReferenceExistsException;
import io.github.alirostom1.smartshop.exception.RessourceNotFoundException;
import io.github.alirostom1.smartshop.model.entity.Product;
import io.github.alirostom1.smartshop.model.mapper.ProductMapper;
import io.github.alirostom1.smartshop.repository.ProductRepository;
import io.github.alirostom1.smartshop.repository.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductInternalResponse createProduct(CreateProductRequest request) {
        validateProductCreation(request);

        Product product = productMapper.requestToEntity(request);
        Product savedProduct = productRepository.save(product);

        return productMapper.entityToInternalResponse(savedProduct);
    }

    @Override
    public ProductInternalResponse updateProduct(UpdateProductRequest request, Long id) {
        Product product = getProductEntity(id);

        productMapper.updateFromRequest(request, product);
        Product updatedProduct = productRepository.save(product);

        return productMapper.entityToInternalResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductEntity(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductInternalResponse> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findByDeletedFalse(pageable);
        return products.map(productMapper::entityToInternalResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductInternalResponse> getFilteredProducts(FilterProductsRequest request, Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.withFilters(request);
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(productMapper::entityToInternalResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInternalResponse getProduct(Long id) {
        Product product = getProductEntity(id);
        return productMapper.entityToInternalResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInternalResponse getProductByReference(String reference) {
        Product product = productRepository.findByReferenceAndDeletedFalse(reference)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with reference: " + reference));
        return productMapper.entityToInternalResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductPublicResponse> getAllProductsPublic(Pageable pageable) {
        Page<Product> products = productRepository.findByDeletedFalse(pageable);
        return products.map(productMapper::entityToPublicResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductPublicResponse> getFilteredProductsPublic(FilterProductsRequest request, Pageable pageable) {
        Specification<Product> spec = ProductSpecifications.withFilters(request);
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(productMapper::entityToPublicResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPublicResponse getProductPublic(UUID publicId) {
        Product product = productRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found"));
        return productMapper.entityToPublicResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPublicResponse getProductByReferencePublic(String reference) {
        Product product = productRepository.findByReferenceAndDeletedFalse(reference)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with reference: " + reference));
        return productMapper.entityToPublicResponse(product);
    }

    // Private helper methods
    private Product getProductEntity(Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new RessourceNotFoundException("Product not found with id: " + id));
    }

    private void validateProductCreation(CreateProductRequest request) {
        if (productRepository.existsByReferenceAndDeletedFalse(request.getReference())) {
            throw new ProductReferenceExistsException("Product reference already exists: " + request.getReference());
        }
    }
}
