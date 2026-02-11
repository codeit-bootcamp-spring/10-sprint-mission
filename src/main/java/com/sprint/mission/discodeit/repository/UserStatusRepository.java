package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.UserStatus;

public interface UserStatusRepository {
	UserStatus save(UserStatus userStatus);

	Optional<UserStatus> findById(UUID id);

	Optional<UserStatus> findByUserId(UUID userId);

	List<UserStatus> findAll();

	void delete(UUID id);
}