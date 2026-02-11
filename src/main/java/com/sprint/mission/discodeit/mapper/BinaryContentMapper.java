package com.sprint.mission.discodeit.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.BinaryContentPostDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;

@Component
public class BinaryContentMapper {
	public BinaryContent fromDto(UUID userId, UUID messageId, BinaryContentDto binaryContentDTO) {
		return new BinaryContent(
			userId,
			messageId,
			binaryContentDTO.fileName()
			// binaryContentDTO.data()
		);
	}

	public BinaryContent fromDto(BinaryContentPostDto binaryContentPostDTO) {
		return new BinaryContent(
			binaryContentPostDTO.userId(),
			binaryContentPostDTO.messageId(),
			binaryContentPostDTO.fileName()
			// binaryContentPostDTO.data() // todo
		);
	}

	public BinaryContentResponseDto toResponseDto(BinaryContent binaryContent) {
		return new BinaryContentResponseDto(
			binaryContent.getUserId(),
			binaryContent.getMessageId(),
			null
		);
	}

}
