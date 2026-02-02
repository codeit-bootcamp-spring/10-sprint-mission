package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.MessagePostDTO;
import com.sprint.mission.discodeit.entity.Message;

@Component
public class MessageMapper {
	public Message toMessage(MessagePostDTO messagePostDTO) {
		return new Message(
			messagePostDTO.userId(),
			messagePostDTO.channelId(),
			messagePostDTO.text(),
			messagePostDTO.attachments()
		);
	}
}
