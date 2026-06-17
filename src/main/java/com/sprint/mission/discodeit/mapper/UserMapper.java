package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    // online 여부는 보안 컴포넌트가 아니라 호출 계층에서 계산해 값으로 전달받는다.
    @Mapping(target = "online", source = "online")
    public abstract UserDto toDto(User user, boolean online);

}
