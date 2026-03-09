package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring",
        uses = {BinaryContentMapper.class})
public interface UserMapper { //MapStruct에게 User -> UserDto 변환 코드를 자동으로 만들어줘라고 지시하는 설정.

    @Mapping(target = "online",ignore = true) //online 변수는 User Entity에 없기때문에 Service 단에서 채우도록 미룸.
    UserDto toDto(User user);
}
