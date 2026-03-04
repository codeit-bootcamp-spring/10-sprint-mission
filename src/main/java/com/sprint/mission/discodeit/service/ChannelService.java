package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto.channelResponse createChannel(ChannelDto.channelCreatePrivateRequest channelPrivateReq);
    ChannelDto.channelResponse createChannel(ChannelDto.channelCreatePublicRequest channelPublicReq);
    ChannelDto.channelResponse findChannel(UUID uuid);
    ChannelDto.channelResponse findChannelByTitle(String title);
    List<ChannelDto.channelResponse> findAllByUserId(UUID userId);
    ChannelDto.channelResponse updateChannel(UUID uuid, ChannelDto.channelUpdatePublicRequest channelReq);
    void deleteChannel(UUID uuid);

//    void joinChannel(UUID channelId, UUID userId);
//    void leaveChannel(UUID channelId, UUID userId);
}
