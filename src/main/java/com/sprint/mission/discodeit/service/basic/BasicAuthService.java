package com.sprint.mission.discodeit.service.basic;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import com.sprint.mission.discodeit.dto.auth.JwtRefreshResult;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionManager;
import com.sprint.mission.discodeit.security.jwt.JwtInformation;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenException;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
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
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtRegistry jwtRegistry;

	@Transactional(readOnly = true)
	@Override
	public JwtRefreshResult refresh(String refreshToken) {
		if (!StringUtils.hasText(refreshToken)) {
			throw new InvalidRefreshTokenException();
		}

		try {
			if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
				throw new InvalidRefreshTokenException();
			}
			if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
				throw new InvalidRefreshTokenException();
			}

			UUID userId = jwtTokenProvider.getUserId(refreshToken);
			User user = userRepository.findById(userId)
				.orElseThrow(InvalidRefreshTokenException::new);

			String accessToken = jwtTokenProvider.generateAccessToken(
				user.getId(),
				user.getUsername(),
				user.getRole()
			);
			String rotatedRefreshToken = jwtTokenProvider.generateRefreshToken(
				user.getId(),
				user.getUsername(),
				user.getRole()
			);
			JwtInformation jwtInformation = new JwtInformation(
				user.getId(),
				accessToken,
				rotatedRefreshToken,
				jwtTokenProvider.getExpiresAt(accessToken),
				jwtTokenProvider.getExpiresAt(rotatedRefreshToken)
			);
			if (!jwtRegistry.rotateJwtInformation(refreshToken, jwtInformation)) {
				throw new InvalidRefreshTokenException();
			}

			UserDto userDto = markOnline(userMapper.toDto(user));

			log.debug("[JWT_REFRESH] 액세스 토큰 재발급 완료: userId={}", userId);
			return new JwtRefreshResult(new JwtDto(userDto, accessToken), rotatedRefreshToken);
		} catch (JwtTokenException | IllegalArgumentException exception) {
			throw new InvalidRefreshTokenException(exception);
		}
	}

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

	private UserDto markOnline(UserDto userDto) {
		return new UserDto(
			userDto.id(),
			userDto.username(),
			userDto.email(),
			userDto.profile(),
			true,
			userDto.role()
		);
	}
}
