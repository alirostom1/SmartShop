package io.github.alirostom1.smartshop.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@SuperBuilder
@Table(name = "products")
@SoftDelete
public class Product extends Auditable{

    @Column(nullable = false,unique = true,updatable = false)
    @Builder.Default
    private UUID publicId = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column(nullable = false)
    private String category;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer stock;

}
