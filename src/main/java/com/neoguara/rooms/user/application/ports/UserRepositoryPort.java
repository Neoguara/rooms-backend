package com.neoguara.rooms.user.application.ports;

import com.neoguara.rooms.user.domain.entities.User;
import com.neoguara.rooms.user.domain.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UserId id);
    List<User> findAll();
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
