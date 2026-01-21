package org.example.repository;

import org.example.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID userId);

    List<User> findAll();

    void deleteById(UUID userId);

    boolean existsById(UUID userId);
}
