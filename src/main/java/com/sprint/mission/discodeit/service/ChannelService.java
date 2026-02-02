package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;

public interface ChannelService {
    Channel createPrivateChannel(ChannelCreatePrivateDTO channelDTO);

    Channel createPublicChannel(ChannelCreatePublicDTO channelDTO);

    ChannelFindDTO findById(UUID id);

    ChannelFindAllDTO findAll();

    Channel updateChannelname(ChannelUpdateDTO channelUpdateDTO);

    void delete(UUID channelId);

//    void joinUser(UUID userId, UUID channelId);
//
//    void quitUser(UUID userId, UUID channelId);

}
