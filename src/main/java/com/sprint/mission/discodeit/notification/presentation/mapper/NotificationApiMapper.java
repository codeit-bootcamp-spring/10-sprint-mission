package com.sprint.mission.discodeit.notification.presentation.mapper;

import com.sprint.mission.discodeit.common.mapper.GenericApiMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface NotificationApiMapper extends GenericApiMapper<Notification, NotificationDto> {

  @Override
  @Mapping(target = "receiverId", source = "receiver.id")
  NotificationDto toDto(Notification entity);
}
