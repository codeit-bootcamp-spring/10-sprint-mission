package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    // online 계산은 @Named 메서드로 분리해서 매핑
    @Mapping(target = "online", source = "userStatus", qualifiedByName = "statusToOnline")
    @Mapping(target = "profile", source = "profile")
    UserDto toDto(User user);

    @Named("statusToOnline")
    default boolean statusToOnline(UserStatus status) {
        return status != null && status.isCurrentlyLoggedIn();
    }

    // 여기 “매칭/검증/인덱싱”은 비즈니스 규칙이라 MapStruct가 자동으로 하기 어려움.
    // 대신 default 메서드에서 인덱싱만 하고, 개별 변환은 위 toResponse(user, status)에 위임.
    default List<UserDto> toDtoList(List<User> users) {

        List<UserDto> result = new ArrayList<>(users.size());
        for (User user : users) {
            UserStatus status = user.getUserStatus();
            if (status == null) {
                throw new IllegalStateException("UserStatus가 없습니다.: userId=" + user.getId());
            }
            result.add(toDto(user));
        }
        return result;
    }
}