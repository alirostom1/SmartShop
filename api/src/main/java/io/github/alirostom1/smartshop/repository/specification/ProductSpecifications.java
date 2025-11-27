package io.github.alirostom1.smartshop.repository.specification;

import io.github.alirostom1.smartshop.dto.request.product.FilterProductsRequest;
import io.github.alirostom1.smartshop.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class ProductSpecifications{
    public static Specification<Product> containsName(String name){
        return (root, query, criteriaBuilder) ->
                StringUtils.hasText(name)
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%" + name.toLowerCase() + "%")
                        : criteriaBuilder.conjunction();
    }
    public static Specification<Product> hasCategory(String category){
        return (root, query, criteriaBuilder) ->
                category != null
                        ? criteriaBuilder.equal(root.get("category"),category)
                        : criteriaBuilder.conjunction();
    }
    public static Specification<Product> priceBetween(BigDecimal minPrice,BigDecimal maxPrice){
        return (root, query, criteriaBuilder) -> {
            if(minPrice == null && maxPrice == null) return criteriaBuilder.conjunction();
            if(minPrice == null) return criteriaBuilder.lessThanOrEqualTo(root.get("unitPrice"),maxPrice);
            if(maxPrice == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("unitPrice"),minPrice);
            return criteriaBuilder.between(root.get("unitPrice"),minPrice,maxPrice);
        };
    }
    public static Specification<Product> hasStock() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("stock"), 0);
    }
    public static Specification<Product> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    public static Specification<Product> withFilters(FilterProductsRequest filter){
        return Specification.where(containsName(filter.getName()))
                .and(hasCategory(filter.getCategory()))
                .and(priceBetween(filter.getMinPrice(),filter.getMaxPrice()))
                .and(filter.getInStockOnly() ? hasStock() : isActive());
    }
}
