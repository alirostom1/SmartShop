package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.product.CreateProductRequest;
import io.github.alirostom1.smartshop.dto.request.product.FilterProductsRequest;
import io.github.alirostom1.smartshop.dto.request.product.UpdateProductRequest;
import io.github.alirostom1.smartshop.dto.response.product.ProductInternalResponse;
import io.github.alirostom1.smartshop.dto.response.product.ProductPublicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService{
    ProductInternalResponse createProduct(CreateProductRequest request);
    ProductInternalResponse updateProduct(UpdateProductRequest request,Long id);
    void deleteProduct(Long id);

    //ADMIN ONLY
    Page<ProductInternalResponse> getAllProducts(Pageable pageable);
    Page<ProductInternalResponse> getFilteredProducts(FilterProductsRequest request, Pageable pageable);
    ProductInternalResponse getProduct(Long id);
    ProductInternalResponse getProductByReference(String reference);

    //CLIENT ONLY
    Page<ProductPublicResponse> getAllProductsPublic(Pageable pageable);
    Page<ProductPublicResponse> getFilteredProductsPublic(FilterProductsRequest request, Pageable pageable);
    ProductPublicResponse getProductPublic(UUID publicId);
    ProductPublicResponse getProductByReferencePublic(String reference);

}
