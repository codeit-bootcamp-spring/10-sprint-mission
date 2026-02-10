package com.sprint.mission.discodeit.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.BinaryContentPostDTO;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;

@Component
public class BinaryContentMapper {
	public BinaryContent fromDto(UUID userId, UUID messageId, BinaryContentDto binaryContentDTO) {
		return new BinaryContent(
			userId,
			messageId,
			binaryContentDTO.fileName(),
			binaryContentDTO.data()
		);
	}

	public BinaryContent fromDto(BinaryContentPostDTO binaryContentPostDTO) {
		return new BinaryContent(
			binaryContentPostDTO.userId(),
			binaryContentPostDTO.messageId(),
			binaryContentPostDTO.fileName(),
			binaryContentPostDTO.data()
		);
	}

	public BinaryContentResponseDTO toResponseDto(BinaryContent binaryContent) {
		return new BinaryContentResponseDTO(
			binaryContent.getUserId(),
			binaryContent.getMessageId(),
			binaryContent.getData()
		);
	}

}
