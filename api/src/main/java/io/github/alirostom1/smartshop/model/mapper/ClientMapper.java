package io.github.alirostom1.smartshop.model.mapper;

import io.github.alirostom1.smartshop.dto.request.client.CreateClientRequest;
import io.github.alirostom1.smartshop.dto.request.client.UpdateClientRequest;
import io.github.alirostom1.smartshop.dto.response.client.ClientInternalResponse;
import io.github.alirostom1.smartshop.dto.response.client.ClientPublicResponse;
import io.github.alirostom1.smartshop.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface ClientMapper{
    @Mapping(target = "loginInfo" ,source = "user")
    ClientInternalResponse entityToInternalResponse(Client client);
    @Mapping(target = "loginInfo" ,source = "user")
    ClientPublicResponse entityToPublicResponse(Client client);

    @Mapping(target = "user.username",source = "username")
    Client requestToEntity(CreateClientRequest request);


    void updateFromRequest(UpdateClientRequest request, @MappingTarget Client client);
}
