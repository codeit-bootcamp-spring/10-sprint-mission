package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.UserStatusType;

import java.util.*;

public class UserMapper {
    public static User toEntity(CreateUserRequestDTO dto, UUID profileImageId) {
        return new User(
                dto.username(),
                dto.email(),
                dto.password(),
                profileImageId
        );
    }

    public static UserResponseDTO toResponse(User user, UserStatus status) {
        boolean online = false;

        if (status.getStatusType().equals(UserStatusType.ONLINE))
        {
            online = true;
        }

        return new UserResponseDTO(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                online
        );
    }

    private static Map<UUID, UserStatus> indexStatusByUserId(List<UserStatus> statuses) {
        Map<UUID, UserStatus> map = new HashMap<>();
        for (UserStatus status: statuses) {
            map.put(status.getUserId(), status);
        }
        return map;
    }

    public static List<UserResponseDTO> toResponseList(List<User> users, List<UserStatus> statuses) {
        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();
        Map<UUID, UserStatus> userStatusMap = indexStatusByUserId(statuses);

        for (User user: users) {
            UserStatus status = userStatusMap.get(user.getId());

            if (status == null) {
                throw new IllegalStateException(
                        "UserStatus가 없습니다.: userId=" + user.getId()
                );
            }

            userResponseDTOS.add(UserMapper.toResponse(user, status));
        }

        return userResponseDTOS;
    }
}
