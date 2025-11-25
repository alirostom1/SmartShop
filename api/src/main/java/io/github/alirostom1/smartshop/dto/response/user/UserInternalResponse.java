package io.github.alirostom1.smartshop.dto.response.user;

import io.github.alirostom1.smartshop.dto.response.common.BaseInternalResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@SuperBuilder
public class UserInternalResponse extends BaseInternalResponse {
    private String username;
    private String role;
}
