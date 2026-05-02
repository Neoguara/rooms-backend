package com.neoguara.rooms.user.infrastructure.repositories;

import com.neoguara.rooms.user.domain.entities.User;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, UserId> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
