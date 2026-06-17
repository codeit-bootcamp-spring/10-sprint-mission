package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class NotificationMapper {

    @Mapping(source = "receiver.id", target = "receiverId")
    public abstract NotificationDto toDto(Notification notification);
}
