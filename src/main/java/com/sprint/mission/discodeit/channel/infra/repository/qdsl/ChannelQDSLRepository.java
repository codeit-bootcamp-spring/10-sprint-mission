package com.sprint.mission.discodeit.channel.infra.repository.qdsl;

import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelQDSLRepository {

  Optional<ChannelDetailDto> findChannelDetailById(UUID id);

  List<ChannelDetailDto> findAllChannelDetails();

  List<ChannelDetailDto> findVisibleChannelDetails(UUID userId);
}
