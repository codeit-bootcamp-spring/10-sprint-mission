package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequsetDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateRequestDto userCreateRequestDto);
    UserResponseDto findUser(UUID userId);
    List<UserResponseDto> findAllUsers();
    UserResponseDto addFriend(UUID senderId, UUID recieverId);
    List<UserResponseDto> findFriends(UUID userId);
    UserResponseDto update(UUID userId, UserUpdateRequsetDto userUpdateRequsetDto);
    void delete(UUID userId);
}
