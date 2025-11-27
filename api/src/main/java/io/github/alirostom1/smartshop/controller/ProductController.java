package io.github.alirostom1.smartshop.controller;

import io.github.alirostom1.smartshop.annotation.AuthZ;
import io.github.alirostom1.smartshop.dto.request.product.CreateProductRequest;
import io.github.alirostom1.smartshop.dto.request.product.FilterProductsRequest;
import io.github.alirostom1.smartshop.dto.request.product.UpdateProductRequest;
import io.github.alirostom1.smartshop.dto.response.common.ApiResponse;
import io.github.alirostom1.smartshop.dto.response.product.ProductInternalResponse;
import io.github.alirostom1.smartshop.dto.response.product.ProductPublicResponse;
import io.github.alirostom1.smartshop.exception.InvalidUUIDException;
import io.github.alirostom1.smartshop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ADMIN ENDPOINTS
    @PostMapping
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<ProductInternalResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            HttpServletRequest httpRequest) {

        ProductInternalResponse response = productService.createProduct(request);
        ApiResponse<ProductInternalResponse> apiResponse = ApiResponse.<ProductInternalResponse>builder()
                .success(true)
                .status(201)
                .message("Product created successfully!")
                .path(httpRequest.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<ProductInternalResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request,
            HttpServletRequest httpRequest) {

        ProductInternalResponse response = productService.updateProduct(request, id);
        ApiResponse<ProductInternalResponse> apiResponse = ApiResponse.<ProductInternalResponse>builder()
                .success(true)
                .status(200)
                .message("Product updated successfully!")
                .path(httpRequest.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            HttpServletRequest request) {

        productService.deleteProduct(id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .status(200)
                .message("Product deleted successfully!")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/admin")
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<Page<ProductInternalResponse>>> getAllProductsAdmin(
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            HttpServletRequest request) {

        Page<ProductInternalResponse> page = productService.getAllProducts(pageable);
        ApiResponse<Page<ProductInternalResponse>> apiResponse = ApiResponse.<Page<ProductInternalResponse>>builder()
                .success(true)
                .status(200)
                .message("Products retrieved successfully!")
                .path(request.getRequestURI())
                .data(page)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/admin/search")
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<Page<ProductInternalResponse>>> searchProductsAdmin(
            @ModelAttribute FilterProductsRequest filter,
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest request) {

        Page<ProductInternalResponse> page = productService.getFilteredProducts(filter, pageable);
        ApiResponse<Page<ProductInternalResponse>> apiResponse = ApiResponse.<Page<ProductInternalResponse>>builder()
                .success(true)
                .status(200)
                .message("Products filtered successfully!")
                .path(request.getRequestURI())
                .data(page)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/admin/{id}")
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<ProductInternalResponse>> getProductAdmin(
            @PathVariable Long id,
            HttpServletRequest request) {

        ProductInternalResponse response = productService.getProduct(id);
        ApiResponse<ProductInternalResponse> apiResponse = ApiResponse.<ProductInternalResponse>builder()
                .success(true)
                .status(200)
                .message("Product retrieved successfully!")
                .path(request.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/admin/reference/{reference}")
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<ProductInternalResponse>> getProductByReferenceAdmin(
            @PathVariable String reference,
            HttpServletRequest request) {

        ProductInternalResponse response = productService.getProductByReference(reference);
        ApiResponse<ProductInternalResponse> apiResponse = ApiResponse.<ProductInternalResponse>builder()
                .success(true)
                .status(200)
                .message("Product retrieved successfully!")
                .path(request.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // PUBLIC ENDPOINTS
    @AuthZ("#user.role.name() == 'CLIENT'")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductPublicResponse>>> getAllProductsPublic(
            @PageableDefault(page = 0, size = 20, sort = "name")
            Pageable pageable,
            HttpServletRequest request) {

        Page<ProductPublicResponse> page = productService.getAllProductsPublic(pageable);
        ApiResponse<Page<ProductPublicResponse>> apiResponse = ApiResponse.<Page<ProductPublicResponse>>builder()
                .success(true)
                .status(200)
                .message("Products retrieved successfully!")
                .path(request.getRequestURI())
                .data(page)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @AuthZ("#user.role.name() == 'CLIENT'")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductPublicResponse>>> searchProductsPublic(
            @ModelAttribute FilterProductsRequest filter,
            @PageableDefault(size = 20) Pageable pageable,
            HttpServletRequest request) {

        Page<ProductPublicResponse> page = productService.getFilteredProductsPublic(filter, pageable);
        ApiResponse<Page<ProductPublicResponse>> apiResponse = ApiResponse.<Page<ProductPublicResponse>>builder()
                .success(true)
                .status(200)
                .message("Products filtered successfully!")
                .path(request.getRequestURI())
                .data(page)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @AuthZ("#user.role.name() == 'CLIENT'")
    @GetMapping("/{publicId}")
    public ResponseEntity<ApiResponse<ProductPublicResponse>> getProductPublic(
            @PathVariable String publicId,
            HttpServletRequest request) {

        UUID uuid;
        try {
            uuid = UUID.fromString(publicId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Invalid product ID format: " + publicId);
        }

        ProductPublicResponse response = productService.getProductPublic(uuid);
        ApiResponse<ProductPublicResponse> apiResponse = ApiResponse.<ProductPublicResponse>builder()
                .success(true)
                .status(200)
                .message("Product retrieved successfully!")
                .path(request.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @AuthZ("#user.role.name() == 'CLIENT'")
    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<ProductPublicResponse>> getProductByReferencePublic(
            @PathVariable String reference,
            HttpServletRequest request) {

        ProductPublicResponse response = productService.getProductByReferencePublic(reference);
        ApiResponse<ProductPublicResponse> apiResponse = ApiResponse.<ProductPublicResponse>builder()
                .success(true)
                .status(200)
                .message("Product retrieved successfully!")
                .path(request.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}