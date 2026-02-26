package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusPostDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

  public ReadStatusResponseDto toResponseDto(ReadStatus readStatus) {
    return new ReadStatusResponseDto(
        readStatus.getId(),
        readStatus.getCreatedAt(),
        readStatus.getUpdatedAt(),
        readStatus.getUserId(),
        readStatus.getChannelId(),
        readStatus.getLastReadTime()
    );
  }

  public ReadStatus fromDto(ReadStatusPostDto readStatusPostDto) {
    return new ReadStatus(
        readStatusPostDto.userId(),
        readStatusPostDto.channelId(),
        Instant.now()
    );
  }
}
