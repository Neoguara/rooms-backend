package com.neoguara.rooms.user.infrastructure.repositories;

import com.neoguara.rooms.user.domain.entities.User;
import com.neoguara.rooms.user.domain.enums.UserStatus;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, UserId> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findAllByStatusNot(UserStatus status);
    Optional<User> findByIdAndStatusNot(UserId id, UserStatus status);
    Optional<User> findByEmailAndStatusNot(String email, UserStatus status);
    boolean existsByEmailAndStatusNot(String email, UserStatus status);
}
