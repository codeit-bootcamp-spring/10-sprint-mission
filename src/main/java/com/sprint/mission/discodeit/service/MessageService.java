package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(UUID channelId, UUID userId, String message);
    // get: uuid로 검색하는건 확실하게 Message반환 or Throw
    Message getMessage(UUID uuid);
    // find: uuid로 검색하지만 다중값이 나오기에 List로 반환
    List<Message> findMessagesByChannelId(UUID uuid);
    List<Message> findAllMessages();
    Message updateMessage(UUID uuid, String newMessage);
    Message updateMessage(Message newMessage);      // File쪽 특화용(다른 JCF관련 필드)
    void deleteMessage(UUID uuid);
}
