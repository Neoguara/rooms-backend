package com.neoguara.rooms.auth;

import com.neoguara.rooms.auth.dto.LoginRequest;
import com.neoguara.rooms.auth.dto.TokenResponse;
import com.neoguara.rooms.auth.infrastructure.security.AuthUserDetails;
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
        var userDetails = (AuthUserDetails) authentication.getPrincipal();
        var data = userDetails.getData();
        return new TokenResponse(
                jwtService.generateToken(userDetails.getUsername()),
                data.id(),
                data.name(),
                data.email(),
                data.role(),
                data.isActive(),
                data.createdAt(),
                data.updatedAt(),
                data.deletedAt()
        );
    }
}
