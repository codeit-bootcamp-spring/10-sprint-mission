package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.entity.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    // 엔티티 -> 응답 DTO 변환
    @Mapping(target = "receiverId", source = "recevier.id")
    NotificationDto toDto(NotificationEntity notification);
}
