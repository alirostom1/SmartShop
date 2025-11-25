package io.github.alirostom1.smartshop.controller;

import io.github.alirostom1.smartshop.dto.request.LoginRequest;
import io.github.alirostom1.smartshop.dto.response.common.ApiResponse;
import io.github.alirostom1.smartshop.dto.response.user.UserPublicResponse;
import io.github.alirostom1.smartshop.enums.UserRole;
import io.github.alirostom1.smartshop.model.entity.User;
import io.github.alirostom1.smartshop.model.mapper.UserMapper;
import io.github.alirostom1.smartshop.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserPublicResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpSession session) {
        User user = authService.login(request);
        session.setAttribute("user", user);
        UserPublicResponse response = userMapper.entityToPublicResponse(user);
        ApiResponse<UserPublicResponse> apiResponse = ApiResponse.<UserPublicResponse>builder()
                .success(true)
                .message("Logged in successfully!")
                .status(200)
                .path(servletRequest.getRequestURI())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

}
