package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // CRUD
    User create(UserCreateRequest req);
    UserResponse find(UUID id);
    List<UserResponse> findAll();
    User updateName(UserUpdateRequest request);
    default void update() {}
    void deleteUser(UUID userID);

    List<Channel> findJoinedChannels(UUID userID);
}
