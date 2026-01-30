package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.dto.user.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserInfoWithProfile createUser(UserCreateInfo userInfo);
    UserInfoWithStatus getUser(UUID userId);
    List<UserInfoWithStatus> getAllUsers();
    List<UserInfo> getUsersByChannelId(UUID channelId);
    UserInfo updateUser(UserUpdateInfo updateInfo);
    void deleteUser(UUID userId);
}
