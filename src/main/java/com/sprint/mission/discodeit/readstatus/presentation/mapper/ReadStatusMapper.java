package com.sprint.mission.discodeit.readstatus.presentation.mapper;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.mapper.GenericMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface ReadStatusMapper extends GenericMapper<ReadStatus, ReadStatusResponse> {

  @Override
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "channelId", source = "channel.id")
  ReadStatusResponse toDto(ReadStatus entity);

  default List<ReadStatus> toEntityFrom(Channel channel,
      List<User> users,
      boolean notificationEnabled) {
    if (channel == null || users == null || users.isEmpty()) {
      return Collections.emptyList();
    }
    return users.stream()
        .map(user -> toEntityFrom(channel, user, notificationEnabled))
        .toList();
  }

  @Mapping(target = "lastReadAt", expression = "java(java.time.Instant.now())")
  ReadStatus toEntityFrom(Channel channel, User user, boolean notificationEnabled);

  ReadStatus partialUpdate(ReadStatusUpdateRequest request, @MappingTarget ReadStatus readStatus);
}
