package com.sprint.mission.discodeit.channel.domain.repository;

import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelQueryRepository {

  Optional<ChannelDetailDto> findChannelDetailById(UUID id);

  List<ChannelDetailDto> findVisibleChannelDetails(UUID userId);
}
