package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UsernameAlreadyExistsException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final BinaryContentRepository binaryContentRepository;
	private final BinaryContentStorage binaryContentStorage;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	@Override
	public UserDto create(UserCreateRequest userCreateRequest,
		Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
		String username = userCreateRequest.username();
		String email = userCreateRequest.email();

		if (userRepository.existsByEmail(email)) {
			throw new UserEmailAlreadyExistsException(email);
		}
		if (userRepository.existsByUsername(username)) {
			throw new UsernameAlreadyExistsException(username);
		}

		BinaryContent nullableProfile = optionalProfileCreateRequest
			.map(profileRequest -> {
				String fileName = profileRequest.fileName();
				String contentType = profileRequest.contentType();
				byte[] bytes = profileRequest.bytes();
				BinaryContent binaryContent = new BinaryContent(fileName,
					(long)bytes.length,
					contentType);
				binaryContentRepository.save(binaryContent);
				binaryContentStorage.put(binaryContent.getId(), bytes);
				return binaryContent;
			})
			.orElse(null);

		String encryptedPassword = passwordEncoder.encode(userCreateRequest.password());

		User user = new User(username, email, encryptedPassword, nullableProfile);
		Instant now = Instant.now();
		UserStatus userStatus = new UserStatus(user, now);

		userRepository.save(user);
		log.info("[USER_CREATE] 사용자 생성 완료: userId={}, email={}", user.getId(), user.getEmail());

		return userMapper.toDto(user);
	}

	@Transactional(readOnly = true)
	@Override
	public UserDto find(UUID userId) {
		return userRepository.findById(userId)
			.map(userMapper::toDto)
			.orElseThrow(() -> new UserNotFoundException(userId));
	}

	@Transactional(readOnly = true)
	@Override
	public List<UserDto> findAll() {
		return userRepository.findAllWithProfileAndStatus()
			.stream()
			.map(userMapper::toDto)
			.toList();
	}

	@Transactional
	@Override
	public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
		Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));

		String newUsername = userUpdateRequest.newUsername();
		String newEmail = userUpdateRequest.newEmail();
		if (userRepository.existsByEmail(newEmail)) {
			throw new UserEmailAlreadyExistsException(newEmail);
		}
		if (userRepository.existsByUsername(newUsername)) {
			throw new UsernameAlreadyExistsException(newUsername);
		}

		BinaryContent nullableProfile = optionalProfileCreateRequest
			.map(profileRequest -> {

				String fileName = profileRequest.fileName();
				String contentType = profileRequest.contentType();
				byte[] bytes = profileRequest.bytes();
				BinaryContent binaryContent = new BinaryContent(fileName,
					(long)bytes.length,
					contentType);
				binaryContentRepository.save(binaryContent);
				binaryContentStorage.put(binaryContent.getId(), bytes);
				return binaryContent;
			})
			.orElse(null);

		String newPassword = userUpdateRequest.newPassword();
		String encryptedNewPassword = newPassword == null ? null : passwordEncoder.encode(newPassword);
		user.update(newUsername, newEmail, encryptedNewPassword, nullableProfile);

		log.info("[USER_UPDATE] 사용자 수정 완료. userId={}", userId);
		return userMapper.toDto(user);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	@Override
	public UserDto updateRole(UserRoleUpdateRequest request) {
		UUID userId = request.userId();
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));

		user.updateRole(request.newRole());
		log.info("[USER_ROLE_UPDATE] 사용자 권한 수정 완료: userId={}, role={}", userId, user.getRole());
		return userMapper.toDto(user);
	}

	@Transactional
	@Override
	public void delete(UUID userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException(userId);
		}

		userRepository.deleteById(userId);
		log.info("[USER_DELETE] 사용자 삭제 완료: userId={}", userId);
	}
}
