package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto.response createUser(UserDto.createRequest userReq, BinaryContentDto.createRequest profileReq);
    UserDto.response findUser(UUID uuid);
    UserDto.response findUserByAccountId(String accountId);
    UserDto.response findUserByMail(String mail);
    List<UserDto.response> findAllUsers();
    UserDto.response updateUser(UUID uuid, UserDto.updateRequest userReq, BinaryContentDto.createRequest profileReq);
    void deleteUser(UUID uuid);
}
