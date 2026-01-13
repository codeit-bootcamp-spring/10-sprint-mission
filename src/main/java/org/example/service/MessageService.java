package org.example.service;

import org.example.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, UUID senderId, UUID channelId);

    Message findById(UUID id);
    List<Message> findAll();
    List<Message> findByChannel(UUID channelId);
    List<Message> findBySender(UUID senderId);

    Message update(UUID id, String content);

    void softDelete(UUID id);

    void hardDelete(UUID id);
}