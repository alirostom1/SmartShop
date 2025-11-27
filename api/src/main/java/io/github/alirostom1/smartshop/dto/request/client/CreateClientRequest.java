package io.github.alirostom1.smartshop.dto.request.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@NoArgsConstructor
public class CreateClientRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "companyName is required")
    private String companyName;

    @Email(message = "email must be valid")
    @NotBlank(message = "email is required")
    private String email;
}
