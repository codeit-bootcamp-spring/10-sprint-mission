package com.sprint.mission.discodeit.service;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.LoginDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;

	User login(LoginDTO loginDTO) {
		User user = userRepository.findByUserName(loginDTO.username())
			.orElseThrow(
				() -> new IllegalArgumentException("사용자명이 일치하지 않습니다.")
			);

		if (user.getPassword().equals(loginDTO.password()))
			return user;

		throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
	}
}
