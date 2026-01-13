package com.sprint.mission.discodeit.service.domain;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class UserDomainService {
    private final UserService userService;
    private final MessageService messageService;

    public UserDomainService(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    public User createUser(CreateUserRequest createUserRequest) {
        User user = new User(
                createUserRequest.nickName(),
                createUserRequest.userName(),
                createUserRequest.email(),
                createUserRequest.phoneNumber()
        );

        userService.save(user);
        return user;
    }

    public User findUserByUserID(UUID userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }

    public List<User> findAllUsers() {
        return userService.findAll();
    }

    public User updateUser(UUID requestId, UpdateUserRequest updateUserRequest) {
        User user = findUserByUserID(requestId);

        user.update(
                updateUserRequest.nickName(),
                updateUserRequest.userName(),
                updateUserRequest.email(),
                updateUserRequest.phoneNumber()
        );

        return user;
    }

    public void deleteUser(UUID requestId) {
        User user = findUserByUserID(requestId);

        user.validateOwner();
        user.clear();
        userService.delete(user);
        messageService.deleteByUser(user);
    }
}
