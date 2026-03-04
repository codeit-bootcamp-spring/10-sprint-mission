package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
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

    @Mapping(target = "online", source = "userStatus", qualifiedByName = "statusToOnline")
    @Mapping(target = "userId", source = "userStatus.user.id")
    UserStatusResponseDTO toResponse(UserStatus userStatus);

    @Named("statusToOnline")
    default boolean statusToOnline(UserStatus status) {
        return status != null && status.isCurrentlyLoggedIn();
    }

    List<UserStatusResponseDTO> toResponseList(List<UserStatus> statuses);
}
