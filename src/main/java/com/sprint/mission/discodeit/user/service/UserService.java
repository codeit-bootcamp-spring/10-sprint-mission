package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.dto.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserInfo createUser(UserCreateInfo userInfo);
    UserInfoWithStatus findUser(UUID userId);
    List<UserInfoWithStatus> findAll();
    List<UserDto> findAllWithUserDTO();
    List<UserInfoWithStatus> findAllByChannelId(UUID channelId);
    UserInfo updateUser(UUID userId, UserUpdateInfo updateInfo);
    void deleteUser(UUID userId);
}
