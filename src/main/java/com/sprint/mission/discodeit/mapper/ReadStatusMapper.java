package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.ReadStatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.response.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {
    // Entity -> DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "channel.id", target = "channelId")
    ReadStatusDTO toDto(ReadStatus readStatus);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)     // 서비스에서 직접 주입
    @Mapping(target = "channel", ignore = true)
    ReadStatus toEntity(ReadStatusCreateRequest request);
}
