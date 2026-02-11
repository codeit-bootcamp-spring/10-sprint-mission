package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;

@Component
public class UserStatusMapper {
	public UserStatusResponseDTO toResponseDto(UserStatus userStatus) {
		return new UserStatusResponseDTO(
			userStatus.getUserId(),
			userStatus.getLastAccessedTime()
		);
	}
}
