package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    // Create
    User createUser(String username, String nickname, String email, String phoneNumber);

    // Read
    Optional<User> findById(UUID userId);
    Optional<User> findByUsername(String username);
    List<User> findAll();

    // Update - Profile
    User updateUsername(UUID id, String newUsername);
    User updateNickname(UUID id, String newNickname);
    User updateEmail(UUID id, String email);
    User updatePhoneNumber(UUID id, String phoneNumber);

    // Update - Status
    User updateUserStatus(UUID userId, UserStatus status);
    User toggleMicrophone(UUID userId, boolean isOn);
    User toggleHeadset(UUID userId, boolean isOn);

    // Delete
    User deleteUser(UUID userId);
}