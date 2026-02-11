package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

// Private 채널 생성 시 필요한 데이터
public record PrivateChannelCreateRequest (
        List<UUID> memberIds,
        ChannelType type
){
}
