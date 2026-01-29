package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createChannel(ChannelCreateRequest request);
    ChannelResponse getChannel(UUID id);
    List<ChannelResponse> findAllByUserId(UUID userId);
    ChannelResponse updateChannel(ChannelUpdateRequest request);
    void deleteChannel(UUID id);
    
    ChannelResponse enterChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}