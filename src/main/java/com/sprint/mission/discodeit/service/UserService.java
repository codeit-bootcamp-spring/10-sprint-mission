package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Role;
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
    void updateUsername(UUID id, String newUsername);
    void updateNickname(UUID id, String newNickname);
    void updateEmail(UUID id, String email);
    void updatePhoneNumber(UUID id, String phoneNumber);

    // Update - Status
    void updateUserStatus(UUID userId, UserStatus status);
    void toggleMicrophone(UUID userId, boolean isOn);
    void toggleHeadset(UUID userId, boolean isOn);

    // Role Management
    void addRoleToUser(UUID userId, UUID roleId);
    void removeRoleFromUser(UUID userId, UUID roleId);
    boolean hasRole(UUID userId, UUID roleId);

    // Delete
    void deleteUser(UUID userId);

    // Setter
    void setMessageService(MessageService messageService);
}