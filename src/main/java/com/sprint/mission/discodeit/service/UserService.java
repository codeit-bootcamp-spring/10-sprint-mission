package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto.Response create(UserDto.Create createRequest);
    UserDto.Response findById(UUID userId);
    List<UserDto.Response> findAll();
    UserDto.Response update(UUID userId, UserDto.Update updateRequest);
    void delete(UUID userId);
}
