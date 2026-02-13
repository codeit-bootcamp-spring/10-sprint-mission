package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.dto.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.channel.dto.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.channel.dto.ChannelResponse;
import com.sprint.mission.discodeit.channel.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPrivate(ChannelCreatePrivateRequest request);
    ChannelResponse createPublic(ChannelCreatePublicRequest request);
    ChannelResponse find(UUID channelId);
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse update(ChannelUpdateRequest request);
    void delete(UUID channelId);
}
