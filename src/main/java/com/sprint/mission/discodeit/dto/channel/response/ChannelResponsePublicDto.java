package com.sprint.mission.discodeit.dto.channel.response;

import com.sprint.mission.discodeit.entity.type.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.channels.Channel;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelResponsePublicDto implements ChannelResponseDto {
    private UUID id;
    private String channelName;
    private List<UUID> messageList;
    private List<UUID> userList;
    private ChannelType channelType;
}
