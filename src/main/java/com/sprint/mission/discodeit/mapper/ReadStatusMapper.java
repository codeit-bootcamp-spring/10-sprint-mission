package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Component;

@Component
public class ReadStatusMapper {

    public ReadStatusResponse toResponse(ReadStatus status){
        return new ReadStatusResponse(
                status.getId(),
                status.getUserId(),
                status.getChannelId(),
                status.getLastReadAt()
        );
    }
}
