package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    // online 계산은 @Named 메서드로 분리해서 매핑
    @Mapping(target = "online", source = "online")
    @Mapping(target = "profile", source = "user.profile")
    UserDto toDto(User user, boolean online);

    default List<UserDto> toDtoList(List<User> users, OnlineChecker onlineChecker) {
        return users.stream()
                .map(user -> toDto(user, onlineChecker.isOnline(user.getId())))
                .toList();
    }

    @FunctionalInterface
    interface OnlineChecker {
        boolean isOnline(UUID userId);
    }
}