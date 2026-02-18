package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.UserStatusPostDTO;
import com.sprint.mission.discodeit.dto.UserStatusResponseDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStatusService {
	private final UserStatusRepository userStatusRepository;
	private final UserRepository userRepository;

	private final UserStatusMapper userStatusMapper;

	public UserStatusResponseDTO create(UserStatusPostDTO userStatusPostDTO) {
		User user = userRepository.findById(userStatusPostDTO.userId())
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + userStatusPostDTO.userId() + "인 유저는 존재하지 않습니다.")
			);

		userStatusRepository.findByUserId(user.getId())
			.ifPresent(ut -> {
				throw new RuntimeException("관련된 UserStatus 객체가 이미 존재합니다.");
			});

		return userStatusMapper.toResponseDto(
			userStatusRepository.save(new UserStatus(userStatusPostDTO.userId()))
		);
	}

	public UserStatusResponseDTO findById(UUID id) {
		return userStatusMapper.toResponseDto(
			userStatusRepository.findById(id)
				.orElseThrow(() ->
					new NoSuchElementException("id가" + id + "인 UserState는 존재하지 않습니다."))
		);
	}

	public List<UserStatusResponseDTO> findAll() {
		return userStatusRepository.findAll().stream()
			.map(userStatusMapper::toResponseDto)
			.collect(Collectors.toList());
	}

	public UserStatusResponseDTO updateByUserId(UUID userId) {
		UserStatus userStatus = userStatusRepository.findByUserId(userId)
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + userId + "인 유저는 존재하지 않습니다.")
			);

		userStatus.updateLastAccessedTime();
		return userStatusMapper.toResponseDto(userStatusRepository.save(userStatus));
	}

	public UserStatusResponseDTO updatedByUserId(UUID userId) {
		UserStatus userStatus = userStatusRepository.findById(userId)
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + userId + "인 유저는 존재하지 않습니다.")
			);

		userStatus.updateLastAccessedTime();
		return userStatusMapper.toResponseDto(userStatusRepository.save(userStatus));
	}

	public void delete(UUID id) {
		userStatusRepository.findById(id).ifPresentOrElse(
			value -> userStatusRepository.delete(id),
			() -> {
				throw new NoSuchElementException("id가 " + id + "인 유저는 존재하지 않습니다.");
			}
		);
	}

}
