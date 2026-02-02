package com.sprint.mission.discodeit.mapper;

import org.springframework.stereotype.Component;

import com.sprint.mission.discodeit.dto.UserPostDTO;
import com.sprint.mission.discodeit.dto.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

@Component
public class UserMapper {
	public User toUser(UserPostDTO userPostDTO) {
		return new User(
			userPostDTO.nickName(),
			userPostDTO.userName(),
			userPostDTO.email(),
			userPostDTO.phoneNumber(),
			userPostDTO.password()
		);
	}

	public UserResponseDTO toUserResponseDTO(User user, UserStatus userStatus) {
		return new UserResponseDTO(
			userStatus, // TODO userstatus 이게 맞나?
			user.getProfileId(),
			user.getNickName(),
			user.getUserName(),
			user.getEmail(),
			user.getPhoneNumber()
		);
	}
}
