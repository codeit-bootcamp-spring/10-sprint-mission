package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public record ChannelFindAllDTO(
        List<ChannelFindDTO> publicChannelList,
        List<ChannelFindDTO> privateChannelList
) {
}
