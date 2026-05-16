package com.neoguara.rooms.user.infrastructure.repositories;

import com.neoguara.rooms.user.application.ports.UserRepositoryPort;
import com.neoguara.rooms.user.domain.entities.User;
import com.neoguara.rooms.user.domain.enums.UserStatus;
import com.neoguara.rooms.user.domain.valueobjects.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findByIdAndStatusNot(id, UserStatus.DELETED);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAllByStatusNot(UserStatus.DELETED);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmailAndStatusNot(email, UserStatus.DELETED);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmailAndStatusNot(email, UserStatus.DELETED);
    }

    @Override
    public void delete(User user) {
        jpaRepository.delete(user);
    }
}
