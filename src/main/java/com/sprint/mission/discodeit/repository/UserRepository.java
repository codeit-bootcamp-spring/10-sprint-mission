package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);

    //검증용
    boolean existsByUsername(String username);
    Optional<User> findByUserNameAndPassword(String  userName, String userPassword);
    boolean existsByEmail(String email);
}
