package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.client.CreateClientRequest;
import io.github.alirostom1.smartshop.dto.request.client.UpdateClientRequest;
import io.github.alirostom1.smartshop.dto.response.client.*;
import io.github.alirostom1.smartshop.enums.ClientTier;
import io.github.alirostom1.smartshop.enums.OrderStatus;
import io.github.alirostom1.smartshop.exception.CompanyNameAlreadyExistsException;
import io.github.alirostom1.smartshop.exception.EmailAlreadyExistsException;
import io.github.alirostom1.smartshop.exception.RessourceNotFoundException;
import io.github.alirostom1.smartshop.exception.UsernameAlreadyExistsException;
import io.github.alirostom1.smartshop.model.entity.Client;
import io.github.alirostom1.smartshop.model.mapper.ClientMapper;
import io.github.alirostom1.smartshop.repository.ClientRepository;
import io.github.alirostom1.smartshop.repository.OrderRepository;
import io.github.alirostom1.smartshop.repository.UserRepository;
import io.github.alirostom1.smartshop.util.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ClientInternalResponse> getClients(Pageable pageable) {
        Page<Client> clients = clientRepository.findAll(pageable);
        return clients.map(clientMapper::entityToInternalResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientInternalResponse getClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Client not found!"));
        return clientMapper.entityToInternalResponse(client);
    }

    @Override
    public ClientInternalResponse createClient(CreateClientRequest request) {
        if(clientRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("Email Already exists!");
        }
        if(clientRepository.existsByCompanyName(request.getCompanyName())){
            throw new CompanyNameAlreadyExistsException("Company name already exists!");
        }
        if(userRepository.existsByUsername(request.getUsername())){
            throw new UsernameAlreadyExistsException("Username already exists!");
        }
        Client client = clientMapper.requestToEntity(request);
        client.getUser().setHashedPassword(passwordEncoder.encode(request.getPassword()));
        Client savedClient = clientRepository.save(client);
        return clientMapper.entityToInternalResponse(savedClient);
    }

    @Override
    public ClientInternalResponse updateClient(UpdateClientRequest request, Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Client not found!"));
        if(clientRepository.existsByEmailAndIdNot(request.getEmail(),id)){
            throw new EmailAlreadyExistsException("Email Already exists!");
        }
        if(clientRepository.existsByCompanyNameAndIdNot(request.getCompanyName(),id)){
            throw new CompanyNameAlreadyExistsException("Company name already exists!");
        }
        clientMapper.updateFromRequest(request,client);
        Client savedClient = clientRepository.save(client);
        return clientMapper.entityToInternalResponse(savedClient);
    }

    @Override
    public void deleteClient(Long id) {
        if(!clientRepository.existsById(id)){
            throw new RessourceNotFoundException("Client not found!");
        }
        clientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientWithStatsInternalResponse getClientWithStats(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RessourceNotFoundException("Client not found!"));

        ClientWithStatsInternalResponse response = clientMapper.entityToStatsInternalResponse(client);
        buildStatsResponse(response,client);
        return response;
    }






    @Override
    @Transactional(readOnly = true)
    public ClientPublicResponse getClientPublic(Long id) {
        Client client = clientRepository.findByUser_Id(id)
                .orElseThrow(() -> new RessourceNotFoundException("Client doesn't exist anymore!"));
        return clientMapper.entityToPublicResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientWithStatsPublicResponse getClientPublicWithStats(Long id){
        Client client = clientRepository.findByUser_Id(id)
                .orElseThrow(() -> new RessourceNotFoundException("Client doesn't exist anymore!"));
        ClientWithStatsPublicResponse response = clientMapper.entityToStatsPublicResponse(client);
        buildStatsResponse(response,client);
        return response;

    }

    private void buildStatsResponse(ClientStatsResponse response,
                                    Client client){
        Long totalOrders = orderRepository.countOrdersByClient(client);
        Long totalConfirmedOrders = orderRepository.countOrdersByClientAndStatus(client,OrderStatus.CONFIRMED);
        BigDecimal totalSpent = orderRepository.sumTotalTTCByClientAndStatus(client,OrderStatus.CONFIRMED);

        LocalDateTime firstOrder = orderRepository.getFirstOrderDateTimeByClient(client);
        LocalDateTime lastOrder = orderRepository.getLastOrderDateTimeByClient(client);

        ClientTier nextTier = client.getTier() == ClientTier.BASIC ? ClientTier.SILVER
                : client.getTier() == ClientTier.SILVER ? ClientTier.GOLD
                : client.getTier() == ClientTier.GOLD ? ClientTier.PLATINUM
                : null;
        BigDecimal amountToNextTier = client.getTier() == ClientTier.BASIC ? BigDecimal.valueOf(1000L).subtract(totalSpent)
                : client.getTier() == ClientTier.SILVER ? BigDecimal.valueOf(5000L).subtract(totalSpent)
                : client.getTier() == ClientTier.GOLD ? BigDecimal.valueOf(15000L).subtract(totalSpent)
                : null;
        Long ordersToNextTier = client.getTier() == ClientTier.BASIC ? Long.valueOf(3L - totalConfirmedOrders)
                : client.getTier() == ClientTier.SILVER ? Long.valueOf(10L - totalConfirmedOrders)
                : client.getTier() == ClientTier.GOLD ? Long.valueOf(20L - totalConfirmedOrders)
                : null;

        response.setTotalOrders(totalOrders);
        response.setTotalConfirmedOrders(totalConfirmedOrders);
        response.setTotalSpent(totalSpent);
        response.setFirstOrder(firstOrder);
        response.setLastOrder(lastOrder);
        response.setNextTier(nextTier);
        response.setAmountToNextTier(amountToNextTier);
        response.setOrdersToNextTier(ordersToNextTier);
    }

}
