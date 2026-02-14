package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto.Response create(UserDto.CreateRequest request);

    UserDto.Response findById(UUID id);
    List<UserDto.Response> findAll();
    List<User> findUsersByChannelId(UUID channelId);

    UserDto.Response update(UserDto.UpdateRequest request);

    void delete(UUID id);
}
