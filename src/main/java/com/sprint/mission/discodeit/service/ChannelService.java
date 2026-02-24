package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelResponse createPublic(PublicChannelCreateRequest createRequest);

  ChannelResponse createPrivate(PrivateChannelCreateRequest createRequest);

  ChannelResponse findById(UUID channelId);

  List<ChannelResponse> findAllByUserId(UUID userId);

  ChannelResponse update(UUID channelId, PublicChannelUpdateRequest updateRequest);

  void delete(UUID channelId);
}
