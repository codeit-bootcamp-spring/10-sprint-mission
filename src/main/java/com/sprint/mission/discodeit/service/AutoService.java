package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;

import java.util.UUID;

public interface AutoService {
    UserDto.UserResponse login(String userName, String password);
    UserDto.UserResponse logout(String userName);
}
