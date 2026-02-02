package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDto.response createUser(UserDto.createRequest userReq, BinaryContentDto.createRequest profileReq);
    // get: uuid로 검색하는건 확실하게 User반환 or Throw
    UserDto.response getUser(UUID uuid);
    // find: 그 외의 필드로 검색하는건 Optional<User>로 호출한 쪽에서 분기처리
    Optional<UserDto.response> findUserByAccountId(String accountId);
    Optional<UserDto.response> findUserByMail(String mail);
    List<UserDto.response> findAllUsers();
    UserDto.response updateUser(UUID uuid, UserDto.updateRequest userReq, BinaryContentDto.createRequest profileReq);
    void deleteUserById(UUID uuid);
}
