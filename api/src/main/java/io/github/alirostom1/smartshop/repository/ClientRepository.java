package io.github.alirostom1.smartshop.repository;

import io.github.alirostom1.smartshop.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long>{
    boolean existsByEmail(String email);
    boolean existsByCompanyName(String name);
    boolean existsByEmailAndIdNot(String email,Long id);
    boolean existsByCompanyNameAndIdNot(String name,Long id);

    Optional<Client> findByUser_Id(Long userId);
}
