package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto find(UUID id);

  List<ChannelDto> findAllByUserId(UUID userId);

  ChannelDto createPublic(PublicChannelCreateRequest request);

  ChannelDto createPrivate(PrivateChannelCreateRequest request);

  ChannelDto update(UUID id, PublicChannelUpdateRequest request);

  void delete(UUID id);
}
