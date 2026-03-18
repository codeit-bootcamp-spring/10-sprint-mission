package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "online", source = "status.online")    // 함수가 is,get형식이어도 프로퍼티(online) 보고 인식함
    UserDto toDto(User user);
}
