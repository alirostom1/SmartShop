package io.github.alirostom1.smartshop.repository;

import io.github.alirostom1.smartshop.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product>{

    boolean existsByReference(String reference);

    Optional<Product> findByPublicId(UUID id);
    Optional<Product> findByReference(String reference);


}
