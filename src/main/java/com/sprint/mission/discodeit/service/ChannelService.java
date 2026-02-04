package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDTO createPrivateChannel(ChannelCreatePrivateDTO channelCreateDTO);

    ChannelDTO createPublicChannel(ChannelCreatePublicDTO channelCreateDTO);

    ChannelDTO findById(UUID channelId);

    List<ChannelDTO> findAll();

    List<ChannelDTO> findAllByUserId(UUID userId);

    ChannelDTO updateChannelname(ChannelUpdateDTO channelUpdateDTO);

    void delete(UUID channelId);

//    void joinUser(UUID userId, UUID channelId);
//
//    void quitUser(UUID userId, UUID channelId);

}
