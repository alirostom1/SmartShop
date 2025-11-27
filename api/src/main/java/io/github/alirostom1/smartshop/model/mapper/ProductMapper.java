package io.github.alirostom1.smartshop.model.mapper;

import io.github.alirostom1.smartshop.dto.request.client.UpdateClientRequest;
import io.github.alirostom1.smartshop.dto.request.product.CreateProductRequest;
import io.github.alirostom1.smartshop.dto.request.product.UpdateProductRequest;
import io.github.alirostom1.smartshop.dto.response.product.ProductInternalResponse;
import io.github.alirostom1.smartshop.dto.response.product.ProductPublicResponse;
import io.github.alirostom1.smartshop.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product requestToEntity(CreateProductRequest request);

    ProductPublicResponse entityToPublicResponse(Product product);
    ProductInternalResponse entityToInternalResponse(Product product);

    void updateFromRequest(UpdateProductRequest request, @MappingTarget Product product);

}
