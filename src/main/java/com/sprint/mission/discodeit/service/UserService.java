package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserRequestDto userCreateDto);
    UserResponseDto find(UUID userId);
    List<UserResponseDto> findAll();
    User update(UUID userId, UserRequestDto userUpdateDto);
    void delete(UUID userId);



}
