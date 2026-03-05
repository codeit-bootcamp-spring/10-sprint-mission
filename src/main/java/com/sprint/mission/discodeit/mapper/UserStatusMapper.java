package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserStatusMapper {

    @Mapping(target = "userId", source = "userStatus.user.id")
    UserStatusDto toDto(UserStatus userStatus);

    List<UserStatusDto> toResponseList(List<UserStatus> statuses);
}
