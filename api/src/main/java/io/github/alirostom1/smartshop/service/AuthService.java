package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.LoginRequest;
import io.github.alirostom1.smartshop.dto.response.user.UserPublicResponse;
import io.github.alirostom1.smartshop.model.entity.User;

public interface AuthService{
    User login(LoginRequest request);
}
