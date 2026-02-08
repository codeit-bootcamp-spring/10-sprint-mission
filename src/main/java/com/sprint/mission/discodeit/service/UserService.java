package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 관련 기능 정의
    UserDto.UserResponse create(UserDto.UserRequest request, String filePath);
    UserDto.UserResponse findById(UUID id);
    List<UserDto.UserResponse> findAll();
    UserDto.UserResponse update(UUID userId, UserDto.UserRequest request, String filePath);
    void delete(UUID userId);
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
    User CheckUser(String name, String password);
    User CheckUserByName(String name);
}
