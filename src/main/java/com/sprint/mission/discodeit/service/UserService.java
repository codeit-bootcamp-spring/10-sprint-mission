package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // 사용자 관련 기능 정의
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
    void create(User user);
    User findById(UUID id);
    List<User> findAll();
    void update(UUID id, String name, String email, String profileImageUrl, String status);
    void delete(UUID id);

}
