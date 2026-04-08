package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(
    componentModel = "spring", // MapStruct가 생성한 구현체를 Spring Bean으로 등록
    imports = {UserStatus.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    User toEntity(UserDto userDto);

    User toEntity(UserPostDto userPostDto);

    @Mapping(target = "online", expression = "java(user.getStatus().isLoggedIn())")
    UserDto toDto(User user);

}
