package io.github.alirostom1.smartshop.dto.request.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UpdateClientRequest{
    @NotBlank(message = "companyName is required")
    private String companyName;

    @Email(message = "email must be valid")
    @NotBlank(message = "email is required")
    private String email;
}
