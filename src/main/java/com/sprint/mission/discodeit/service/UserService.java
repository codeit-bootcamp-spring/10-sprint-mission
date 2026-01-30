package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserInfoDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // Create
    User create(UserCreateDto request);

    // Read
    UserInfoDto findById(UUID id);

    // ReadAll
    List<UserInfoDto> findAll();

    // Update
    User update(UserUpdateDto request);

    // User가 보낸 모든 메시지 조회
    List<Message> getUserMessages(UUID id);

    // 내가 속한 채널들 조회
    List<Channel> getUserChannels(UUID id);

    // Delete
    void delete(UUID id);

    public void updateLastActiveTime(UUID id);
}
