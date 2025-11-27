package io.github.alirostom1.smartshop.dto.response.client;

import io.github.alirostom1.smartshop.dto.response.common.BaseInternalResponse;
import io.github.alirostom1.smartshop.dto.response.user.UserInternalResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@SuperBuilder
public class ClientInternalResponse extends BaseInternalResponse {
    private String companyName;
    private String email;
    private String tier;
    private UserInternalResponse loginInfo;
}
