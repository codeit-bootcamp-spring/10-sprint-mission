package com.sprint.mission.discodeit.dto.channel.response;

import com.sprint.mission.discodeit.entity.type.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelResponsePrivateDto implements ChannelResponseDto{
    private UUID id;
    private String channelName;
    private List<UUID> userList;
    private List<UUID> messageList;
    private ChannelType channelType;
}
