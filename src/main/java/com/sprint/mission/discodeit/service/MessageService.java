package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    // Create
    Message sendMessage(UUID authorId, UUID channelId, String content);

    // Read
    List<Message> findAllByChannelId(UUID channelId);
    Optional<Message> findById(UUID messageId);
    List<Message> findAll(UUID authorId);


    // Update
    void updateMessage(UUID messageId, String newContent);

    // Delete
    void deleteMessage(UUID messageId);

    // Delete All(유저 탈퇴, 채널 삭제 등)
    void deleteMessagesByChannelId(UUID channelId);
    void deleteMessagesByAuthorId(UUID authorId);
}