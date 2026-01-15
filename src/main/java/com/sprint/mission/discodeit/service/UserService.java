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
    User findById(UUID userId);
    Optional<User> findByUsername(String username);
    List<User> findAll();

    // Update - Profile
    User updateUsername(UUID userId, String newUsername);
    User updateNickname(UUID userId, String newNickname);
    User updateEmail(UUID userId, String email);
    User updatePhoneNumber(UUID userId, String phoneNumber);

    // Update - Status
    User updateUserStatus(UUID userId, UserStatus status);
    User toggleMicrophone(UUID userId, boolean isOn);
    User toggleHeadset(UUID userId, boolean isOn);

    // Delete
    User deleteUser(UUID userId);

    // Logic
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}