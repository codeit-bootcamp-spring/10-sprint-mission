package org.example.service;

import org.example.entity.Channel;
import org.example.entity.Status;
import org.example.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String password, String nickname);

    User findById(UUID id);
    List<User> findAll();

    User update(UUID userId, String username, String email, String nickname, String password, Status status);
//    User updateProfile(UUID id, String username, String email, String nickname);
//
//    void changePassword(UUID id, String newPassword);
//
//    void changeStatus(UUID id, Status status);

    void softDelete(UUID id);

    void hardDelete(UUID id);

    List<Channel> findChannelByUser(UUID id);
}