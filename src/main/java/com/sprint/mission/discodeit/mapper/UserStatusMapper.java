package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {
    // user 엔티티에서 id꺼내서 DTO의 userId로 매핑
    @Mapping(target="userId", source = "user.id")
    UserStatusDto toDto(UserStatus userStatus);
}
