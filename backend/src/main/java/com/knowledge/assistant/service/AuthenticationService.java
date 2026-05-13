package com.knowledge.assistant.service;

import com.knowledge.assistant.dto.AuthenticationRequest;
import com.knowledge.assistant.dto.AuthenticationResponse;
import com.knowledge.assistant.dto.RegisterRequest;
import com.knowledge.assistant.dto.UserDto;
import com.knowledge.assistant.entity.Role;
import com.knowledge.assistant.entity.User;
import com.knowledge.assistant.repository.UserRepository;
import com.knowledge.assistant.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        
        userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setUser(mapToUserDto(user));
        return response;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(jwtToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setUser(mapToUserDto(user));
        return response;
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        return userDto;
    }
}
