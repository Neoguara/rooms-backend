package com.neoguara.rooms.auth;

import java.util.Optional;

public interface UserAuthPort {
    Optional<AuthUserData> findByEmail(String email);
}
