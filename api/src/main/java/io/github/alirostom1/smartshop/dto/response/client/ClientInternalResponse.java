package io.github.alirostom1.smartshop.dto.response.client;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.github.alirostom1.smartshop.dto.response.common.BaseInternalResponse;
import io.github.alirostom1.smartshop.dto.response.user.UserInternalResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@SuperBuilder
@JsonPropertyOrder({
        "id",
        "createdAt",
        "updatedAt",
        "companyName",
        "email",
        "tier",
        "loginInfo"
})
public class ClientInternalResponse extends BaseInternalResponse {
    private String companyName;
    private String email;
    private String tier;
    private UserInternalResponse loginInfo;
}
