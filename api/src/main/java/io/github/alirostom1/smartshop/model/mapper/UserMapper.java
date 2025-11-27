package io.github.alirostom1.smartshop.model.mapper;


import io.github.alirostom1.smartshop.dto.request.client.CreateClientRequest;
import io.github.alirostom1.smartshop.dto.response.user.UserInternalResponse;
import io.github.alirostom1.smartshop.dto.response.user.UserPublicResponse;
import io.github.alirostom1.smartshop.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserPublicResponse entityToPublicResponse(User user);
    UserInternalResponse entityToInternalResponse(User user);


    User requestToEntity(CreateClientRequest request);
}
