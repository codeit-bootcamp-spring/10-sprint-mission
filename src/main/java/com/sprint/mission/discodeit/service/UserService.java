package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import java.util.List;


public interface UserService {
    User create(String name, String nickname, String email, String password);

    User findById(UUID id);

    List<User> findAll();

    User update(UUID id, String name, String nickname, String email, UUID profileId, String password);

    void delete(UUID id);

    List<Channel> findJoinedChannelsByUserId(UUID userId); // 특정 유저가 참가한 채널 목록 조회

    List<Message> findMessagesByUserId(UUID userId); // 특정 유저가 발행한 메시지 목록 조회
}
