package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserInfoWithProfile createUser(UserCreateInfo userInfo);
    UserInfoWithStatus findUser(UUID userId);
    List<UserInfoWithStatus> findAll();
    List<UserInfoWithStatus> findAllByChannelId(UUID channelId);
    UserInfo updateUser(UserUpdateInfo updateInfo);
    void deleteUser(UUID userId);
}
