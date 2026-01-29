package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.UserPatchDTO;
import com.sprint.mission.discodeit.dto.UserPostDTO;
import com.sprint.mission.discodeit.dto.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
	private final UserMapper userMapper;

	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentRepository binaryContentRepository;

	@Override
	public User create(UserPostDTO userPostDTO) {
		// username과 email이 다른 유저와 같으면 안 된다.
		if (isUserNameDuplicated(userPostDTO.userName()) ||
			isEmailDuplicated(userPostDTO.email()))
			throw new IllegalArgumentException("중복된 사용자명 또는 이메일입니다.");

		User newUser = userMapper.toUser(userPostDTO);
		// UserStatus를 같이 생성합니다.
		userStatusRepository.save(
			new UserStatus(newUser.getId(), Instant.now())
		);

		return userRepository.save(newUser);
	}

	public boolean isUserNameDuplicated(String userName) {
		return userRepository.findAll().stream()
			.anyMatch(user -> user.getUserName().equals(userName));
	}

	public boolean isEmailDuplicated(String email) {
		return userRepository.findAll().stream()
			.anyMatch(user -> user.getEmail().equals(email));
	}

	@Override
	public UserResponseDTO findById(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다."));
		UserStatus userStatus = userStatusRepository.findByUserId(userId);

		return userMapper.toUserResponseDTO(user, userStatus);
	}

	@Override
	public UserResponseDTO findByUserName(String userName) {
		User user = userRepository.findByUserName(userName)
			.orElseThrow(
				() -> new NoSuchElementException("사용자명이 " + userName + "인 유저를 찾을 수 없습니다.")
			);
		UserStatus userStatus = userStatusRepository.findByUserId(user.getId());

		return userMapper.toUserResponseDTO(user, userStatus);
	}

	@Override
	public List<UserResponseDTO> findAll() {
		return userRepository.findAll().stream()
			.map(user -> userMapper.toUserResponseDTO(
					user, userStatusRepository.findByUserId(user.getId())
				)
			).collect(Collectors.toList());
	}

	@Override
	public User updateUser(UserPatchDTO userPatchDTO) {
		User updatedUser = userRepository.findById(userPatchDTO.id())
			.orElseThrow(() -> new NoSuchElementException("id가 " + userPatchDTO.id() + "인 유저를 찾을 수 없습니다."));

		// input이 null이 아닌 필드 업데이트
		Optional.ofNullable(userPatchDTO.updateData().getProfileId())
			.ifPresent(updatedUser::updateProfileId);
		Optional.ofNullable(userPatchDTO.updateData().getNickName())
			.ifPresent(updatedUser::updateNickName);
		Optional.ofNullable(userPatchDTO.updateData().getUserName())
			.ifPresent(updatedUser::updateUserName);
		Optional.ofNullable(userPatchDTO.updateData().getEmail())
			.ifPresent(updatedUser::updateEmail);
		Optional.ofNullable(userPatchDTO.updateData().getPhoneNumber())
			.ifPresent(updatedUser::updatePhoneNumber);

		return userRepository.save(updatedUser);
	}

	@Override
	public void delete(UUID userId) {
		UserResponseDTO user = findById(userId);
		userRepository.delete(userId);

		// 관련된 도메인도 함께 삭제
		userStatusRepository.delete(user.userStatus().getId());
		binaryContentRepository.delete(user.profileId());
	}
}
