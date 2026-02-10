package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.UserPatchDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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
	private final BinaryContentMapper binaryContentMapper;

	private final UserRepository userRepository;
	private final UserStatusRepository userStatusRepository;
	private final BinaryContentRepository binaryContentRepository;

	@Override
	public UserResponseDto create(UserPostDto userPostDTO) {
		// username과 email이 다른 유저와 같으면 안 된다.
		if (isUserNameDuplicated(userPostDTO.userName()) ||
			isEmailDuplicated(userPostDTO.email()))
			throw new IllegalArgumentException("중복된 사용자명 또는 이메일입니다.");

		// 새 user 객체 생성
		User newUser = userMapper.toUser(userPostDTO);

		// 프로필 정보를 선택적으로 저장
		Optional.ofNullable(userPostDTO.profileImage())
			.ifPresent(profileImage -> {
					BinaryContent binaryContent = binaryContentRepository.save(
						binaryContentMapper.fromDto(newUser.getId(), null, profileImage));
					newUser.updateProfileId(binaryContent.getId()); // user에 프로필 정보 업데이트
				}
			);

		// UserStatus를 같이 생성 및 저장
		UserStatus userStatus = userStatusRepository.save(new UserStatus(newUser.getId()));

		return userMapper.toUserResponseDTO(userRepository.save(newUser));
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
	public UserResponseDto findById(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다."));
		UserStatus userStatus = userStatusRepository.findByUserId(userId)
			.orElseThrow(() -> new NoSuchElementException("userId가 " + userId + "인 UserStatus를 찾을 수 없습니다."));

		return userMapper.toUserResponseDTO(user);
	}

	@Override
	public UserResponseDto findByUserName(String userName) {
		User user = userRepository.findByUserName(userName)
			.orElseThrow(
				() -> new NoSuchElementException("사용자명이 " + userName + "인 유저를 찾을 수 없습니다.")
			);
		UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
			.orElseThrow(() -> new NoSuchElementException("userId가 " + user.getId() + "인 UserStatus를 찾을 수 없습니다."));

		return userMapper.toUserResponseDTO(user);
	}

	@Override
	public List<UserResponseDto> findAll() {
		return userRepository.findAll().stream()
			.map(userMapper::toUserResponseDTO
			).collect(Collectors.toList());
	}

	@Override
	public UserResponseDto updateUser(UUID userId, UserPatchDto userPatchDto) {
		User updatedUser = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다."));

		/**
		 * 유저 정보 업데이트
		 * todo: Binarycontent를 수정 불가능한 도메인이니까 새로 생성해줘야 하나?
		 */
		Optional.ofNullable(userPatchDto.binaryContentDTO())
			.ifPresent(
				binaryContentDTO -> {
					BinaryContent binaryContent = binaryContentRepository.save(
						binaryContentMapper.fromDto(updatedUser.getId(), null, binaryContentDTO)
					);
					updatedUser.updateProfileId(binaryContent.getId());
				}
			);
		// Optional.ofNullable(userPatchDto.profileId())
		// 	.ifPresent(updatedUser::updateProfileId);
		Optional.ofNullable(userPatchDto.nickName())
			.ifPresent(updatedUser::updateNickName);
		Optional.ofNullable(userPatchDto.email())
			.ifPresent(updatedUser::updateEmail);
		Optional.ofNullable(userPatchDto.phoneNumber())
			.ifPresent(updatedUser::updatePhoneNumber);
		Optional.ofNullable(userPatchDto.password())
			.ifPresent(updatedUser::updatePassword);
		// todo: binarycontent 업데이트

		return userMapper.toUserResponseDTO(userRepository.save(updatedUser));
	}

	@Override
	public void delete(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("id가" + userId + "인 유저는 존재하지 않습니다."));
		userRepository.delete(userId);

		// 관련된 도메인도 함께 삭제
		userStatusRepository.findByUserId(userId)
			.ifPresent(userStatus -> userStatusRepository.delete(userStatus.getId()));
		Optional.ofNullable(user.getProfileId()).ifPresent(binaryContentRepository::delete);

	}
}
