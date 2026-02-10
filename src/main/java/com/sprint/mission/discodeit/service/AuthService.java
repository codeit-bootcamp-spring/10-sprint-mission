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

		// 해당 user의 userStatus가 존재한다면 lastAccessedTime을 업데이트, 없다면 새로 만들어 저장한다.
		userStatusRepository.findByUserId(user.getId()).ifPresentOrElse(
			userStatus -> {
				userStatus.updateLastAccessedTime();
				userStatusRepository.save(userStatus);
			},
			() -> userStatusRepository.save(new UserStatus(user.getId()))
		);

		if (user.getPassword().equals(loginDto.password()))
			return userMapper.toUserResponseDto(user, true);

		throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
	}
}
