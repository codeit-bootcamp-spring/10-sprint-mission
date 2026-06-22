package com.sprint.mission.discodeit.readstatus.application.mapper;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface ReadStatusDomainMapper {

  @Mapping(target = "notificationEnabled", constant = "false")
  @Mapping(target = "lastReadAt", source = "request.lastReadAt")
  ReadStatus toEntity(ReadStatusCreateRequest request, User user, Channel channel);

  ReadStatus toEntityFrom(Channel channel, User user, boolean notificationEnabled);

  void partialUpdate(ReadStatusUpdateRequest dto, @MappingTarget ReadStatus entity);

  default List<ReadStatus> toEntityFrom(Channel channel, List<User> users,
      boolean notificationEnabled) {
    if (users == null) {
      return Collections.emptyList();
    }
    return users.stream()
        .map(user -> toEntityFrom(channel, user, notificationEnabled))
        .toList();
  }
}
