package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {
    Message createMessage(UUID channelID, UUID userId, String content );

    Message getMessage(UUID id);

    List<Message> getAllMessages();

    Message updateMessage(String content, UUID id);

    void deleteMessage(UUID id);

    List<Message> getMessagesByChannelId(UUID channelId); // 특정 채널 메시지 조회

    List<Message> getMessagesByUserId(UUID userId); // 특정 유저 메시지 조회

}
