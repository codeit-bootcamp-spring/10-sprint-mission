package com.sprint.mission.discodeit.notification.presentation.mapper;

import com.sprint.mission.discodeit.common.mapper.GenericMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface NotificationMapper extends GenericMapper<Notification, NotificationDto> {

  Notification toEntityFrom(User receiver, String title, String content);

  default List<Notification> toEntityFrom(List<User> receivers, String title, String content) {
    if (receivers == null || receivers.isEmpty()) {
      return Collections.emptyList();
    }
    return receivers.stream()
        .map(receiver -> toEntityFrom(receiver, title, content))
        .toList();
  }
}
