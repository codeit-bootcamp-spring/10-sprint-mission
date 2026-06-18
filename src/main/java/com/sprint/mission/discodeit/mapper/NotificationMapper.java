package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = UserMapper.class)
public interface NotificationMapper {

  @Mapping(target = "receiverId", source = "receiver.id")
  NotificationDto toDto(Notification entity);
}
