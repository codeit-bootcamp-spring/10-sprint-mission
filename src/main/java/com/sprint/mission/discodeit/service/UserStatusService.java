package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.UserStatusPatchDTO;
import com.sprint.mission.discodeit.dto.UserStatusPostDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStatusService {
	@Qualifier("JCFUserStatusRepository")
	private final UserStatusRepository userStatusRepository;
	private final UserRepository userRepository;

	public UserStatus create(UserStatusPostDTO userStatusPostDTO) {
		User user = userRepository.findById(userStatusPostDTO.userId())
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + userStatusPostDTO.userId() + "인 유저는 존재하지 않습니다.")
			);

		userStatusRepository.findByUserId(user.getId())
			.ifPresent(ut -> {
				throw new RuntimeException("관련된 UserStatus 객체가 이미 존재합니다.");
			});

		return userStatusRepository.save(new UserStatus(userStatusPostDTO.userId()));
	}

	public UserStatus findById(UUID id) {
		return userStatusRepository.findById(id)
			.orElseThrow(() ->
				new NoSuchElementException("id가" + id + "인 UserState는 존재하지 않습니다."));
	}

	public List<UserStatus> findAll() {
		return userStatusRepository.findAll();
	}

	public UserStatus update(UserStatusPatchDTO userStatusPatchDTO) {
		UserStatus userStatus = userStatusRepository.findById(userStatusPatchDTO.userStatusId())
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + userStatusPatchDTO.userStatusId() + "인 유저는 존재하지 않습니다.")
			);

		userStatus.updateLastAccessedTime();
		return userStatusRepository.save(userStatus);
	}

	public UserStatus updatedByUserId(UUID userId) {
		UserStatus userStatus = userStatusRepository.findById(userId)
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + userId + "인 유저는 존재하지 않습니다.")
			);

		userStatus.updateLastAccessedTime();
		return userStatusRepository.save(userStatus);
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
