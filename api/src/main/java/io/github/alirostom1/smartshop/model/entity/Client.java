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
    private String companyName;
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ClientTier tier = ClientTier.BASIC;

    @OneToOne
    @JoinColumn(name = "user_id",unique = true,nullable = false)
    private User user;
}
