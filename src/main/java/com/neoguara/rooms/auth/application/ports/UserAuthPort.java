package com.neoguara.rooms.auth.application.ports;

import java.util.Optional;

public interface UserAuthPort {
    Optional<AuthUserData> findByEmail(String email);
}
