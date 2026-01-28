package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserFindingDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(UserCreateRequestDto userCreateRequestDto);
    UserFindingDto find(UUID userId);
    List<UserFindingDto> findAll();
    User update(UserUpdateRequestDto userUpdateRequestDto);
    void delete(UUID userId);
}
