package com.sprint.mission.discodeit.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.ReadStatusPostDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

@Component
public class ReadStatusMapper {
	public ReadStatusResponseDto toResponseDto(ReadStatus readStatus) {
		return new ReadStatusResponseDto(
			readStatus.getId(),
			readStatus.getUserId(),
			readStatus.getChannelId(),
			readStatus.getLastReadTime()
		);
	}

	public ReadStatus fromDto(ReadStatusPostDto readStatusPostDTO) {
		return new ReadStatus(
			readStatusPostDTO.userId(),
			readStatusPostDTO.channelId(),
			Instant.now()
		);
	}
}
