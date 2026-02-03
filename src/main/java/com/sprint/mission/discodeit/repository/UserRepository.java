package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;

public interface UserRepository {
	User save(User user);

	Optional<User> findById(UUID id);

	Optional<User> findByUserName(String userName);

	List<User> findAll();

	void delete(UUID id);
}
