package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReadStatusMapper {
    public ReadStatusResponseDto toDto(ReadStatus readStatus){
        if (readStatus == null) return null;

        return new ReadStatusResponseDto(readStatus.getId(),
                                readStatus.getUserId(),
                                readStatus.getChannelId(),
                                readStatus.getLastReadTime());
    }
}
