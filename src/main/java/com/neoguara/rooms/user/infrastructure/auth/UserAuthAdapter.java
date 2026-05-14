package com.neoguara.rooms.user.infrastructure.auth;

import com.neoguara.rooms.auth.application.ports.AuthUserData;
import com.neoguara.rooms.auth.application.ports.UserAuthPort;
import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAuthAdapter implements UserAuthPort {

    private final UserRepositoryPort userRepository;

    public UserAuthAdapter(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<AuthUserData> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new AuthUserData(
                        user.getId().id(),
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole().name(),
                        user.getActive(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.getDeletedAt()
                ));
    }
}
