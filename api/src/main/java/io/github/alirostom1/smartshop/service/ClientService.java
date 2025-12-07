package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.client.CreateClientRequest;
import io.github.alirostom1.smartshop.dto.request.client.UpdateClientRequest;
import io.github.alirostom1.smartshop.dto.response.client.ClientInternalResponse;
import io.github.alirostom1.smartshop.dto.response.client.ClientPublicResponse;
import io.github.alirostom1.smartshop.dto.response.client.ClientWithStatsInternalResponse;
import io.github.alirostom1.smartshop.dto.response.client.ClientWithStatsPublicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public interface ClientService{

    //INTERNAL METHODS(FOR ADMIN)
    Page<ClientInternalResponse> getClients(Pageable pageable);
    ClientInternalResponse getClient(Long id);
    ClientInternalResponse createClient(CreateClientRequest request);
    ClientInternalResponse updateClient(UpdateClientRequest request, Long id);
    void deleteClient(Long id);
    ClientWithStatsInternalResponse getClientWithStats(Long id);

    //PUBLIC METHODS(FOR CLIENT)
    ClientPublicResponse getClientPublic(Long id);
    ClientWithStatsPublicResponse getClientPublicWithStats(Long id);
}
