package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreatePrivateRequest;
import com.sprint.mission.discodeit.dto.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ChannelService {

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  ChannelDto createPublicChannel(ChannelCreatePublicRequest request);

  ChannelDto createPrivateChannel(ChannelCreatePrivateRequest request);

  ChannelDto findById(UUID channelId);

  List<ChannelDto> findAllByUserId(UUID userId);

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  ChannelDto update(UUID channelId, ChannelUpdateRequest request);

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  void delete(UUID id);
}
