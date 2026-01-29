package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateDto userCreateDto);
    UserResponseDto findUser(UUID userId);
    List<UserResponseDto> findAllUsers();
    UserResponseDto addFriend(UUID senderId, UUID recieverId);
    List<UserResponseDto> findFriends(UUID userId);
    UserResponseDto update(UUID userId, UserUpdateDto userUpdateDto);
    void delete(UUID userId);
    void save(User user);
}
