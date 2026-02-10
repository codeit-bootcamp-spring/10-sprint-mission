package com.sprint.mission.discodeit.service;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final UserMapper userMapper;

	public UserResponseDto login(LoginDto loginDto) {
		User user = userRepository.findByUserName(loginDto.userName())
			.orElseThrow(
				() -> new IllegalArgumentException("사용자명이 일치하지 않습니다.")
			);

		// 해당 user의 userstatus를 찾지 못하면 새로 만들도록 하낟.
		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
			.orElse(userStatusRepository.save(new UserStatus(user.getId())));

		if (user.getPassword().equals(loginDto.password()))
			return userMapper.toUserResponseDTO(user);

		throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
	}
}
