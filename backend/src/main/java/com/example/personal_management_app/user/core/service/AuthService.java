package com.example.personal_management_app.user.core.service;

import com.example.personal_management_app.core.services.JwtService;
import com.example.personal_management_app.user.core.dtos.AuthResponse;
import com.example.personal_management_app.user.core.dtos.CreateUserRequest;
import com.example.personal_management_app.user.core.dtos.LoginRequest;
import com.example.personal_management_app.user.core.dtos.RegisterRequest;
import com.example.personal_management_app.user.core.dtos.UserDto;
import com.example.personal_management_app.user.core.exception.InvalidCredentialsException;
import com.example.personal_management_app.user.core.exception.UserEmailExistsException;
import com.example.personal_management_app.user.core.infrastructure.mapstruct.UserMapper;
import com.example.personal_management_app.user.core.infrastructure.persistence.entities.User;
import com.example.personal_management_app.user.core.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserEmailExistsException(request.getEmail());
        }

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName(request.getName().trim());
        createUserRequest.setEmail(request.getEmail().trim());
        createUserRequest.setPassword(request.getPassword());

        UserDto user = userService.createUser(createUserRequest);
        return issueToken(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        UserDto userDto = userMapper.toDto(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, userDto);
    }

    @Transactional
    public AuthResponse issueToken(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
            .orElseThrow(InvalidCredentialsException::new);
        return new AuthResponse(jwtService.generateToken(user), userDto);
    }
}
