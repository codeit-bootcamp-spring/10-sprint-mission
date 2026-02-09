package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.dto.UserCreateInfo;
import com.sprint.mission.discodeit.user.dto.UserInfo;
import com.sprint.mission.discodeit.user.dto.UserInfoWithStatus;
import com.sprint.mission.discodeit.user.dto.UserUpdateInfo;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserInfo createUser(UserCreateInfo userInfo);
    UserInfoWithStatus findUser(UUID userId);
    List<UserInfoWithStatus> findAll();
    List<UserInfoWithStatus> findAllByChannelId(UUID channelId);
    UserInfo updateUser(UserUpdateInfo updateInfo);
    void deleteUser(UUID userId);
}
