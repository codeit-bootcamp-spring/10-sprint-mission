package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto.ChannelResponsePublic createPublic(ChannelDto.ChannelRequest request);
    ChannelDto.ChannelResponsePrivate createPrivate(List<UUID> users);
    void delete(UUID channelId);
    ChannelDto.ChannelResponse findById(UUID channelId);
    List<ChannelDto.ChannelResponsePrivate> findAllPrivateChannels();
    List<ChannelDto.ChannelResponsePublic> findAllPublicChannels();
    List<ChannelDto.ChannelResponse> findAllByUserId(UUID userId);
    ChannelDto.ChannelResponsePublic update(UUID id, ChannelDto.ChannelRequest request);
    Channel CheckChannel(String name);
}
