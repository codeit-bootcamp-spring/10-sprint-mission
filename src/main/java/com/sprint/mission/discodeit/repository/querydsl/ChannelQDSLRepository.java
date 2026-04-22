package com.sprint.mission.discodeit.repository.querydsl;

import com.sprint.mission.discodeit.dto.ChannelDetailResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelQDSLRepository {

  Optional<ChannelDetailResponse> findChannelDetailById(UUID id);

  List<ChannelDetailResponse> findAllChannelDetails();

  List<ChannelDetailResponse> findVisibleChannelDetails(UUID userId);
}
