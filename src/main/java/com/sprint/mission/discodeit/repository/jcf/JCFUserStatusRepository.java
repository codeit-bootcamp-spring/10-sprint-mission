package com.sprint.mission.discodeit.repository.jcf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JCFUserStatusRepository implements UserStatusRepository {
	private final List<UserStatus> data;

	@Override
	public UserStatus save(UserStatus userStatus) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getId().equals(userStatus.getId())) {
				data.set(i, userStatus);
				return userStatus;
			}
		}

		data.add(userStatus);
		return userStatus;
	}

	@Override
	public Optional<UserStatus> findById(UUID id) {
		return data.stream()
			.filter(userStatus -> userStatus.getId().equals(id))
			.findFirst();
	}

	@Override
	public Optional<UserStatus> findByUserId(UUID userId) {
		return data.stream()
			.filter(userStatus -> userStatus.getUserId().equals(userId))
			.findFirst();
	}

	@Override
	public List<UserStatus> findAll() {
		return data;
	}

	@Override
	public void delete(UUID id) {
		data.removeIf(userStatus -> userStatus.getId().equals(id));
	}
}
