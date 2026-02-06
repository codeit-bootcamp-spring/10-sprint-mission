package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateInfo;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusInfo;
import com.sprint.mission.discodeit.entity.ReadStatus;

public class ReadStatusMapper {
    private ReadStatusMapper(){}

    public static ReadStatusInfo toReadStatusInfo(ReadStatus readStatus) {
        return new ReadStatusInfo(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }

    public static ReadStatusCreateInfo toReadStatusCreateInfo(ReadStatus readStatus) {
        return new ReadStatusCreateInfo(
                readStatus.getUserId(),
                readStatus.getChannelId()
        );
    }

    public static ReadStatus toReadStatus(ReadStatusCreateInfo readStatusCreateInfo) {
        return new ReadStatus(
                readStatusCreateInfo.userId(),
                readStatusCreateInfo.channelId()
        );
    }
}
