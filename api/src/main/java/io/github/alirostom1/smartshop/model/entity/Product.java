package io.github.alirostom1.smartshop.model.entity;

import io.github.alirostom1.smartshop.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@SuperBuilder
@Table(name = "products")
public class Product extends Auditable{

    @Version
    private Integer version;

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

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer reservedStock = 0;

    public Integer getAvailableStock(){
        return stock - reservedStock;
    }

    public void reserveStock(Integer quantity){
        if(getAvailableStock() < quantity){
            throw new InsufficientStockException("Insufficient available stock");
        }
        this.reservedStock += quantity;
    }
    public void releaseReservation(Integer quantity){
        if(this.reservedStock < quantity){
            throw new InsufficientStockException("Cannot release more than reserved !");
        }
        this.reservedStock -= quantity;
    }
    public void confirmReservation(Integer quantity){
        releaseReservation(quantity);
        if(this.stock < quantity){
            throw new InsufficientStockException("Insufficient stock !");
        }
        this.stock -= quantity;
    }
}
