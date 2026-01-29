package com.sprint.mission.discodeit.repository.file;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

// @Repository
public class FileUserStatusRepository implements UserStatusRepository {
	@Override
	public UserStatus save(UserStatus userStatus) {
		return null;
	}

	@Override
	public UserStatus findByUserId(UUID userId) {
		return null;
	}

	@Override
	public void delete(UUID id) {

	}
}
