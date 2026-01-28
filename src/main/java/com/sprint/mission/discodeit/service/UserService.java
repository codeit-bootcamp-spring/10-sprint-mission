package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserDto.CreateRequest request);
    UserDto.Response find(UUID userId);
    List<UserDto.Response> findAll();
    User update(UUID userId, UserDto.UpdateRequest request);
    void delete(UUID userId);
}
