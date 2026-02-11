package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;

@Component
public class UserMapper {
	public User toUser(UserPostDto userPostDTO) {
		return new User(
			userPostDTO.nickName(),
			userPostDTO.userName(),
			userPostDTO.email(),
			userPostDTO.phoneNumber(),
			userPostDTO.password()
		);
	}

	public UserResponseDto toUserResponseDto(User user, boolean online) {
		return new UserResponseDto(
			user.getId(),
			user.getCreatedAt(),
			user.getUpdatedAt(),
			user.getUserName(),
			user.getEmail(),
			user.getProfileId(),
			online
		);
	}
}
