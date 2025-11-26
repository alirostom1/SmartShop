package io.github.alirostom1.smartshop.model.entity;

import io.github.alirostom1.smartshop.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "users")
public class User extends Auditable{

    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false)
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.CLIENT;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Client client;
}
