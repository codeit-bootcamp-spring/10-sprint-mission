package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequestDTO req, BinaryContentDto profileDto);

    UserDto find(UUID userId);

    List<UserDto> findAll();

    UserDto update(UUID userId, UserUpdateDTO userUpdateDTO, BinaryContentDto profileDto);

    void delete(UUID userId);
}
