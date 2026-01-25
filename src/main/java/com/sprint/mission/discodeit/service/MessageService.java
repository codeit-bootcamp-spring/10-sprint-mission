package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.*;

public interface MessageService {
    Message createMessage(String content, UUID userId, UUID channelId);
    Message findMessageById(UUID messageId);
    List<Message> findAllMessagesByChannelId(UUID channelId);
//    List<Message> findAllMessagesByUserId(UUID userId);
//    List<Message> findAllMessagesByContent(String content);
    Message updateMessage(UUID messageId, String newContent);
    void deleteMessage(UUID messageId);
    void deleteAllMessagesByUserId(UUID userId);
    void deleteAllMessagesByChannelId(UUID channelId);
}