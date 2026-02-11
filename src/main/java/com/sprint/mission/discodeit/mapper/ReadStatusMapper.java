package com.sprint.mission.discodeit.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.ReadStatusPostDTO;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;

@Component
public class ReadStatusMapper {
	public ReadStatusResponseDTO toResponseDto(ReadStatus readStatus) {
		return new ReadStatusResponseDTO(
			readStatus.getUserId(),
			readStatus.getChannelId(),
			readStatus.getLastReadTime()
		);
	}

	public ReadStatus fromDto(ReadStatusPostDTO readStatusPostDTO) {
		return new ReadStatus(
			readStatusPostDTO.userId(),
			readStatusPostDTO.channelId(),
			Instant.now()
		);
	}
}
