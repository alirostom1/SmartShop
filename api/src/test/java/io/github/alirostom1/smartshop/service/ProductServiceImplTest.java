package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.product.CreateProductRequest;
import io.github.alirostom1.smartshop.dto.request.product.UpdateProductRequest;
import io.github.alirostom1.smartshop.dto.response.product.ProductInternalResponse;
import io.github.alirostom1.smartshop.dto.response.product.ProductPublicResponse;
import io.github.alirostom1.smartshop.exception.ProductReferenceExistsException;
import io.github.alirostom1.smartshop.exception.RessourceNotFoundException;
import io.github.alirostom1.smartshop.model.entity.Product;
import io.github.alirostom1.smartshop.model.mapper.ProductMapper;
import io.github.alirostom1.smartshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_Success() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("Laptop")
                .reference("PROD-001")
                .category("Electronics")
                .unitPrice(new BigDecimal("15000"))
                .stock(50)
                .build();

        Product product = Product.builder().id(1L).build();
        ProductInternalResponse response = ProductInternalResponse.builder()
                .id(1L)
                .name("Laptop")
                .build();

        when(productRepository.existsByReferenceAndDeletedFalse("PROD-001")).thenReturn(false);
        when(productMapper.requestToEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.entityToInternalResponse(product)).thenReturn(response);

        ProductInternalResponse result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository).existsByReferenceAndDeletedFalse("PROD-001");
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_ReferenceExists_ThrowsException() {
        CreateProductRequest request = CreateProductRequest.builder()
                .reference("PROD-001")
                .build();

        when(productRepository.existsByReferenceAndDeletedFalse("PROD-001")).thenReturn(true);

        assertThrows(ProductReferenceExistsException.class,
                () -> productService.createProduct(request));
    }

    @Test
    void updateProduct_Success() {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .name("Updated Laptop")
                .unitPrice(new BigDecimal("16000"))
                .stock(60)
                .build();

        Product existingProduct = Product.builder().id(1L).build();
        ProductInternalResponse response = ProductInternalResponse.builder().id(1L).build();

        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(productMapper.entityToInternalResponse(existingProduct)).thenReturn(response);

        ProductInternalResponse result = productService.updateProduct(request, 1L);

        assertNotNull(result);
        verify(productMapper).updateFromRequest(request, existingProduct);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateProduct_NotFound_ThrowsException() {
        UpdateProductRequest request = UpdateProductRequest.builder().build();
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(RessourceNotFoundException.class,
                () -> productService.updateProduct(request, 1L));
    }

    @Test
    void deleteProduct_Success() {
        Product product = Product.builder().id(1L).deleted(false).build();
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertTrue(product.getDeleted());
        verify(productRepository).save(product);
    }

    @Test
    void getProduct_Success() {
        Product product = Product.builder().id(1L).build();
        ProductInternalResponse response = ProductInternalResponse.builder().id(1L).build();

        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(product));
        when(productMapper.entityToInternalResponse(product)).thenReturn(response);

        ProductInternalResponse result = productService.getProduct(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getProduct_NotFound_ThrowsException() {
        when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(RessourceNotFoundException.class,
                () -> productService.getProduct(1L));
    }

    @Test
    void getAllProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = Product.builder().id(1L).build();
        Page<Product> productPage = new PageImpl<>(List.of(product));
        ProductInternalResponse response = ProductInternalResponse.builder().id(1L).build();

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(productPage);
        when(productMapper.entityToInternalResponse(product)).thenReturn(response);

        Page<ProductInternalResponse> result = productService.getAllProducts(pageable);

        assertEquals(1, result.getTotalElements());
        verify(productRepository).findByDeletedFalse(pageable);
    }

    @Test
    void getProductPublic_Success() {
        UUID publicId = UUID.randomUUID();
        Product product = Product.builder().id(1L).publicId(publicId).build();
        ProductPublicResponse response = new ProductPublicResponse(
                publicId.toString(), "REF", "Name", "Category", "Desc", BigDecimal.TEN, 10
        );

        when(productRepository.findByPublicIdAndDeletedFalse(publicId)).thenReturn(Optional.of(product));
        when(productMapper.entityToPublicResponse(product)).thenReturn(response);

        ProductPublicResponse result = productService.getProductPublic(publicId);

        assertEquals(publicId.toString(), result.publicId());
    }

    @Test
    void getProductByReference_Success() {
        Product product = Product.builder().id(1L).build();
        ProductInternalResponse response = ProductInternalResponse.builder().id(1L).build();

        when(productRepository.findByReferenceAndDeletedFalse("PROD-001")).thenReturn(Optional.of(product));
        when(productMapper.entityToInternalResponse(product)).thenReturn(response);

        ProductInternalResponse result = productService.getProductByReference("PROD-001");

        assertNotNull(result);
        verify(productRepository).findByReferenceAndDeletedFalse("PROD-001");
    }
}