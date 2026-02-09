package com.sprint.mission.discodeit.readstatus;

import com.sprint.mission.discodeit.readstatus.dto.ReadStatusCreateInfo;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusInfo;

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
