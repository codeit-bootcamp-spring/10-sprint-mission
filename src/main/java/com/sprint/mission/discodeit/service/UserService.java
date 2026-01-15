package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

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
    User updateUserProfile(UUID userId, String newUsername, String newNickname, String newEmail, String newPhoneNumber);
    void updateUsername(UUID userId, String newUsername);
    void updateNickname(UUID userId, String newNickname);
    void updateEmail(UUID userId, String email);
    void updatePhoneNumber(UUID userId, String phoneNumber);

    // Update - Status
    public User updateUserStatus(UUID userId, User.UserPresence newPresence, Boolean newMicrophoneIsOn, Boolean newHeadsetIsOn);
    void updatePresence(UUID userId, User.UserPresence presence);
    void toggleMicrophone(UUID userId, boolean isOn);
    void toggleHeadset(UUID userId, boolean isOn);

    // Delete
    User deleteUser(UUID userId);

    // Logic
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}