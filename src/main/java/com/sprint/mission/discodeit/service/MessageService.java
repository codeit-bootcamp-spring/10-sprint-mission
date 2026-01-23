package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

    // Create
    Message sendMessage(UUID authorId, UUID channelId, String content);

    //    // Read - Global Scan
    //    List<Message> findAllByChannelId(UUID channelId);
    //    List<Message> findAllByUserId(UUID userId);

    // Read - Direct Access
    List<Message> findMessagesByChannel(UUID channelId);
    List<Message> findMessagesByAuthor(UUID authorId);

    // Read - Single
    Message findById(UUID messageId);


    // Update
    Message updateMessage(UUID messageId, String newContent);

    // Delete
    void deleteMessage(UUID messageId);

    // Delete All(유저 탈퇴, 채널 삭제 등)
    void deleteMessagesByChannelId(UUID channelId);
    void deleteMessagesByAuthorId(UUID authorId);

    // logic
    void save(Message message);
}