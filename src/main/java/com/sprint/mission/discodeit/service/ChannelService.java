package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

import java.util.UUID;

public interface ChannelService {
    ChannelDTO createPrivateChannel(ChannelCreatePrivateDTO channelCreateDTO);

    ChannelDTO createPublicChannel(ChannelCreatePublicDTO channelCreateDTO);

    ChannelFindDTO findById(UUID id);

    ChannelFindAllDTO findAll();

    ChannelDTO updateChannelname(ChannelUpdateDTO channelUpdateDTO);

    void delete(UUID channelId);

//    void joinUser(UUID userId, UUID channelId);
//
//    void quitUser(UUID userId, UUID channelId);

}
