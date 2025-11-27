package io.github.alirostom1.smartshop.dto.response.client;

import io.github.alirostom1.smartshop.dto.response.user.UserPublicResponse;
import io.github.alirostom1.smartshop.enums.ClientTier;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@SuperBuilder
public class ClientPublicResponse{
    private String companyName;
    private String email;
    private String tier;
    private UserPublicResponse loginInfo;
}
