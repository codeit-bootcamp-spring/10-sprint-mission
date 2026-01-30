package com.sprint.mission.discodeit.dto.channel;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class ChannelCreatePrivateDto {
    private String channelName;
    private List<UUID> userList;

}
