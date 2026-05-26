package com.sprint.mission.discodeit.service.basic;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final SessionManager sessionManager;

	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	@Override
	public UserDto updateRole(UserRoleUpdateRequest request) {
		return updateRoleInternal(request);
	}

	private UserDto updateRoleInternal(UserRoleUpdateRequest request) {
		UUID userId = request.userId();
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));

		Role previousRole = user.getRole();
		Role newRole = request.newRole();
		user.updateRole(newRole);

		if (newRole != null && !previousRole.equals(newRole)) {
			sessionManager.invalidateSessionsByUserId(userId);
		}

		log.info("[USER_ROLE_UPDATE] 사용자 권한 수정 완료: userId={}, role={}", userId, user.getRole());
		return userMapper.toDto(user);
	}
}
