package io.github.alirostom1.smartshop.controller;

import io.github.alirostom1.smartshop.annotation.AuthN;
import io.github.alirostom1.smartshop.annotation.AuthZ;
import io.github.alirostom1.smartshop.dto.request.client.CreateClientRequest;
import io.github.alirostom1.smartshop.dto.request.client.UpdateClientRequest;
import io.github.alirostom1.smartshop.dto.response.client.ClientInternalResponse;
import io.github.alirostom1.smartshop.dto.response.client.ClientPublicResponse;
import io.github.alirostom1.smartshop.dto.response.common.ApiResponse;
import io.github.alirostom1.smartshop.model.entity.Client;
import io.github.alirostom1.smartshop.model.entity.User;
import io.github.alirostom1.smartshop.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController{
    private final ClientService clientService;

    @GetMapping
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<Page<ClientInternalResponse>>> getAll(
            @PageableDefault(page = 0, size = 5,sort = "createdAt",direction = Sort.Direction.DESC)
            Pageable pageable,
            HttpServletRequest request
    ){
        Page<ClientInternalResponse> page = clientService.getClients(pageable);
        ApiResponse<Page<ClientInternalResponse>> apiResponse = ApiResponse.<Page<ClientInternalResponse>>builder()
                .success(true)
                .status(200)
                .message("Clients retrieved successfully!")
                .path(request.getRequestURI())
                .data(page)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<ClientInternalResponse>> getClient(
            @PathVariable Long id,
            HttpServletRequest request
    ){
        ClientInternalResponse response = clientService.getClient(id);
        ApiResponse<ClientInternalResponse> apiResponse = ApiResponse.<ClientInternalResponse>builder()
                .success(true)
                .status(200)
                .message("Client retrieved successfully!")
                .path(request.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<ClientInternalResponse>> createClient(
            @Valid @RequestBody CreateClientRequest request,
            HttpServletRequest httpRequest
    ){
        ClientInternalResponse response = clientService.createClient(request);
        ApiResponse<ClientInternalResponse> apiResponse = ApiResponse.<ClientInternalResponse>builder()
                .success(true)
                .status(201)
                .message("Client created successfully!")
                .path(httpRequest.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PutMapping("/{id}")
    @AuthZ("#user.role.name() == 'ADMIN'")
    public ResponseEntity<ApiResponse<ClientInternalResponse>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientRequest request,
            HttpServletRequest httpRequest
            ){
        ClientInternalResponse response = clientService.updateClient(request,id);
        ApiResponse<ClientInternalResponse> apiResponse = ApiResponse.<ClientInternalResponse>builder()
                .success(true)
                .status(201)
                .data(response)
                .path(httpRequest.getRequestURI())
                .message("Client updated successfully!")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(
            @PathVariable Long id,
            HttpServletRequest request
    ){
        clientService.deleteClient(id);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(true)
                .status(200)
                .path(request.getRequestURI())
                .message("Client deleted successfully!")
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }


    @AuthZ("#user.role.name() == 'CLIENT'")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ClientPublicResponse>> me(
            HttpServletRequest request,
            HttpSession session
    ){
        ClientPublicResponse response = clientService.getClientPublic(((User)session.getAttribute("user")).getId());
        ApiResponse<ClientPublicResponse> apiResponse = ApiResponse.<ClientPublicResponse>builder()
                .success(true)
                .status(200)
                .path(request.getRequestURI())
                .message("Successfully retrieved client!")
                .data(response)
                .build();
        return ResponseEntity.ok().body(apiResponse);
    }

}
