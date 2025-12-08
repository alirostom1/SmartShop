package io.github.alirostom1.smartshop.repository;

import io.github.alirostom1.smartshop.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product>{

    boolean existsByReferenceAndDeletedFalse(String reference);

    Optional<Product> findByPublicIdAndDeletedFalse(UUID id);
    Optional<Product> findByReferenceAndDeletedFalse(String reference);

    Page<Product> findByDeletedFalse(Pageable pageable);
    Page<Product> findAllByDeletedFalse(Specification<Product> spec,Pageable pageable);
    Optional<Product> findByIdAndDeletedFalse(Long id);

}
