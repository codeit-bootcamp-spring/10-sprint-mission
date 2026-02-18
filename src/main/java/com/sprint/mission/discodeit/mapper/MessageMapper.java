package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageMapper {
	private final BinaryContentRepository binaryContentRepository;

	public Message toMessage(MessagePostDto messagePostDto) {
		return new Message(
			messagePostDto.authorId(),
			messagePostDto.channelId(),
			messagePostDto.text(),
			null
		);
	}

	public MessageResponseDto toResponse(Message message) {
		return new MessageResponseDto(
			message.getId(),
			message.getText(),
			message.getAuthorId(),
			message.getChannelId()
		);
	}

}
