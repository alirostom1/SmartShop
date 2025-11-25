package io.github.alirostom1.smartshop.service;

import io.github.alirostom1.smartshop.dto.request.LoginRequest;
import io.github.alirostom1.smartshop.dto.response.user.UserPublicResponse;
import io.github.alirostom1.smartshop.exception.AuthenticationException;
import io.github.alirostom1.smartshop.model.entity.User;
import io.github.alirostom1.smartshop.model.mapper.UserMapper;
import io.github.alirostom1.smartshop.repository.UserRepository;
import io.github.alirostom1.smartshop.util.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials!"));
        if(!passwordEncoder.matches(request.password(),user.getHashedPassword())){
            throw new AuthenticationException("Invalid credentials!");
        }
        return user;
    }
}
