package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto.response createChannel(ChannelDto.createPrivateRequest channelPrivateReq);
    ChannelDto.response createChannel(ChannelDto.createPublicRequest channelPublicReq);
    ChannelDto.response findChannel(UUID uuid);
    ChannelDto.response findChannelByTitle(String title);
    List<ChannelDto.response> findAllByUserId(UUID userId);
    ChannelDto.response updateChannel(UUID uuid, ChannelDto.updatePublicRequest channelReq);
    void deleteChannel(UUID uuid);

    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}
