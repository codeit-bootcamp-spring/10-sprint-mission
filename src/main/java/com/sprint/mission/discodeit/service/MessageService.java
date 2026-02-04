package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageInfoDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // Create
    MessageInfoDto create(MessageCreateDto messageCreateDto);

    // Read
    MessageInfoDto findById(UUID id);

    // ReadAll
    List<MessageInfoDto> findAllByChannelId(UUID channelId);

    // Update
    MessageInfoDto update(MessageUpdateDto messageUpdateDto);

    List<MessageInfoDto> searchMessage(UUID channelId, String keyword);

    List<MessageInfoDto> getUserMessages(UUID id);

    List<MessageInfoDto> getChannelMessages(UUID channelId);

//    UUID sendDirectMessage(UUID senderId, UUID receiverId, String content);

    // Delete
    void delete(UUID id);

}
