package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // Setter
    void setChannelService(ChannelService channelService);
    void setUserService(UserService userService);
    // CRUD
    Message create(String contents, UUID userID, UUID channelID);
    Message find(UUID messageID);
    List<Message> findAll();
    Message update(UUID messageID, String contents);
    void deleteMessage(UUID messageID);

    // 도메인 별 메시지 조회
    List<String> findMessagesByChannel(UUID channelID);
    List<String> findMessagesByUser(UUID userID);
}
