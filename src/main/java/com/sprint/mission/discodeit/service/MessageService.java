package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageInfoDto;
import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface MessageService {
    // Create
    MessageInfoDto create(MessageCreateDto messageCreateDto);

    // Read
    Message findById(UUID id);

    // ReadAll
    List<Message> readAll();

    // Update
    Message update(UUID messageId, String newContent);

    List<Message> searchMessage(UUID channelId, String keyword);

//    UUID sendDirectMessage(UUID senderId, UUID receiverId, String content);

    // Delete
    void delete(UUID id);

}
