package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.MessagePostDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageMapper {
	private final BinaryContentRepository binaryContentRepository;

	public Message toMessage(MessagePostDTO messagePostDTO) {
		return new Message(
			messagePostDTO.userId(),
			messagePostDTO.channelId(),
			messagePostDTO.text(),
			null
		);
	}
}
