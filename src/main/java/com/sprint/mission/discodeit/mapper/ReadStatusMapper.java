package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatusInfoDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

    // ReadStatus -> ReadStatusInfoDto
    public ReadStatusInfoDto toReadStatusInfoDto(ReadStatus readStatus) {
        return new ReadStatusInfoDto(readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt());
    }
}
