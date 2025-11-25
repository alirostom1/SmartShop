package io.github.alirostom1.smartshop.dto.response.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter @Setter
@SuperBuilder
public abstract class BaseInternalResponse{
    private Long id;
    private String createdAt;
    private String updatedAt;
}
