package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {BinaryContentMapper.class})
public abstract class UserMapper {
    // online 필드를 채워야 되기 때문에 user의 status가 들고있는 isOnline()메서드를 사용해야함
    @Mapping(source = "status.isOnline()", target = "online")
    public abstract UserDto toDto(User user);
}
