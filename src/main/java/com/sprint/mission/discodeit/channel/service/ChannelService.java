package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto create(ChannelCreatePublicRequest request);

  ChannelDto create(ChannelCreatePrivateRequest request);

  ChannelDto find(UUID channelId);

  List<ChannelDto> findByUserId(UUID userId);

  ChannelDto update(UUID channelId, ChannelUpdateRequest request);

  void delete(UUID channelId);
}
