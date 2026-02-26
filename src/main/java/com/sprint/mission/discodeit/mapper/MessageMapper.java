package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  private final BinaryContentRepository binaryContentRepository;

  public Message toMessage(MessagePostDto messagePostDto) {
    return new Message(
        messagePostDto.authorId(),
        messagePostDto.channelId(),
        messagePostDto.content(),
        new ArrayList<>()
    );
  }

  public MessageResponseDto toResponse(Message message) {
    return new MessageResponseDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannelId(),
        message.getAuthorId(),
        message.getAttachmentIds()
    );
  }

}
