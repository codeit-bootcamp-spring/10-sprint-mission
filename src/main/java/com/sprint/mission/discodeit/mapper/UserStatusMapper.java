package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.ArrayList;
import java.util.List;

public class UserStatusMapper {
    public static UserStatus toEntity(CreateUserStatusRequestDTO dto) {
        return new UserStatus(
                dto.userId(),
                dto.lastLoginAt()
        );
    }

    public static UserStatusResponseDTO toResponse(UserStatus userStatus) {
        return new UserStatusResponseDTO(
                userStatus.getUserId(),
                userStatus.getId(),
                userStatus.getStatusType(),
                userStatus.getLastLoginAt()
        );
    }

    public static List<UserStatusResponseDTO> toResponseList(List<UserStatus> statuses) {
        List<UserStatusResponseDTO> dtos = new ArrayList<>();

        for (UserStatus status : statuses) {
            dtos.add(UserStatusMapper.toResponse(status));
        }

        return dtos;
    }
}
