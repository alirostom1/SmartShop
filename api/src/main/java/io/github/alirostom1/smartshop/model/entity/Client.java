package io.github.alirostom1.smartshop.model.entity;

import io.github.alirostom1.smartshop.enums.ClientTier;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
public class Client extends Auditable{
    @Column(nullable = false,unique = true)
    private String companyName;
    @Column(nullable = false,unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ClientTier tier = ClientTier.BASIC;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id",unique = true,nullable = false)
    private User user;
}
