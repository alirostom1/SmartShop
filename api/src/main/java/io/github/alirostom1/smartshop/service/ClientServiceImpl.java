package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.client.CreateClientRequest;
import io.github.alirostom1.smartshop.dto.request.client.UpdateClientRequest;
import io.github.alirostom1.smartshop.dto.response.client.ClientInternalResponse;
import io.github.alirostom1.smartshop.dto.response.client.ClientPublicResponse;
import io.github.alirostom1.smartshop.exception.CompanyNameAlreadyExistsException;
import io.github.alirostom1.smartshop.exception.EmailAlreadyExistsException;
import io.github.alirostom1.smartshop.exception.RessourceNotFoundException;
import io.github.alirostom1.smartshop.exception.UsernameAlreadyExistsException;
import io.github.alirostom1.smartshop.model.entity.Client;
import io.github.alirostom1.smartshop.model.mapper.ClientMapper;
import io.github.alirostom1.smartshop.repository.ClientRepository;
import io.github.alirostom1.smartshop.repository.UserRepository;
import io.github.alirostom1.smartshop.util.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService{
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final BCryptPasswordEncoder passwordEncoder;

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
    public ClientPublicResponse getClientPublic(Long id) {
        Client client = clientRepository.findByUser_Id(id)
                .orElseThrow(() -> new RessourceNotFoundException("Client doesn't exist anymore!"));
        return clientMapper.entityToPublicResponse(client);
    }
}
