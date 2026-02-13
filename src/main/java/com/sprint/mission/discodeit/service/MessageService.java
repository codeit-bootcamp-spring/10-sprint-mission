package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

  // Create
  MessageResponseDto create(MessageCreateDto messageCreateDto);

  // Read
  MessageResponseDto findById(UUID id);

  // ReadAll
  List<MessageResponseDto> findAllByChannelId(UUID channelId);

  // Update
  MessageResponseDto update(UUID id, MessageUpdateDto messageUpdateDto);

  List<MessageResponseDto> searchMessage(UUID channelId, String keyword);

  List<MessageResponseDto> getUserMessages(UUID id);

//    List<MessageResponseDto> getChannelMessages(UUID channelId);

//    UUID sendDirectMessage(UUID senderId, UUID receiverId, String bytes);

  // Delete
  void delete(UUID id);

}
