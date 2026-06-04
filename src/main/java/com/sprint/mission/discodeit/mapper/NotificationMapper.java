package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface NotificationMapper {

  @Mapping(target = "receiverId", source = "receiver.id")
  NotificationDto toDto(Notification entity);
}
