package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    @Transactional
    UserDto create(UserCreateRequest userCreateRequest,
                   BinaryContentCreateRequest profileDto);

    UserDto find(UUID userId);

    List<UserDto> findAll();

    UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
                          BinaryContentCreateRequest profileRequest);

    void delete(UUID userId);
}
