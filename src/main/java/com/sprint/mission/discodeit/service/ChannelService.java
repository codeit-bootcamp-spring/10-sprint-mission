package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto.Response createPublic(ChannelDto.CreatePublic createRequest);

  ChannelDto.Response createPrivate(ChannelDto.CreatePrivate createRequest);

  ChannelDto.Response findById(UUID channelId);

  List<ChannelDto.Response> findAllByUserId(UUID userId);

  ChannelDto.Response update(UUID channelId, ChannelDto.Update updateRequest);

  void delete(UUID channelId);
}
