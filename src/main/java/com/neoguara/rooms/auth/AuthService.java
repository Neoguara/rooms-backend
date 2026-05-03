package com.neoguara.rooms.auth;

import com.neoguara.rooms.auth.dto.LoginRequest;
import com.neoguara.rooms.auth.dto.TokenResponse;
import com.neoguara.rooms.user.application.mappers.UserMapper;
import com.neoguara.rooms.user.domain.entities.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public TokenResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var authentication = authenticationManager.authenticate(authToken);
        var user = (User) authentication.getPrincipal();
        var userResponse = UserMapper.toResponse(user);
        return new TokenResponse(
                jwtService.generateToken(user.getUsername()),
                userResponse.id(),
                userResponse.name(),
                userResponse.email(),
                userResponse.role(),
                userResponse.isActive(),
                userResponse.createdAt(),
                userResponse.updatedAt(),
                userResponse.deletedAt()
        );
    }
}
