package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto.userResponse createUser(UserDto.userCreateRequest userReq, BinaryContentDto.binaryContentCreateRequest profileReq);
    UserDto.userResponse findUser(UUID uuid);
    UserDto.userResponse findUserByUsername(String username);
    UserDto.userResponse findUserByEmail(String mail);
    List<UserDto.userResponse> findAllUsers();
    UserDto.userResponse updateUser(UUID uuid, UserDto.userUpdateRequest userReq, BinaryContentDto.binaryContentCreateRequest profileReq);
    void deleteUser(UUID uuid);
}
