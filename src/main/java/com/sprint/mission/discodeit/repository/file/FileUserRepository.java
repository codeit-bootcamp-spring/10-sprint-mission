package com.sprint.mission.discodeit.repository.file;

import static com.sprint.mission.discodeit.util.FilePath.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FileIo;

@Repository
public class FileUserRepository implements UserRepository {
	private final FileIo<User> fileIo;

	private FileUserRepository() {
		fileIo = new FileIo<User>(USER_DIRECTORY);
		fileIo.init();
	}

	@Override
	public User save(User user) {
		return fileIo.save(user.getId(), user);
	}

	@Override
	public Optional<User> findById(UUID id) {
		return fileIo.load().stream()
			.filter(u -> u.getId().equals(id))
			.findFirst();
	}

	@Override
	public Optional<User> findByUserName(String userName) {
		return fileIo.load().stream()
			.filter(u -> u.getUserName().equals(userName))
			.findFirst();
	}

	@Override
	public List<User> findAll() {
		return fileIo.load();
	}

	@Override
	public void delete(UUID userId) throws RuntimeException {
		fileIo.delete(userId);
	}
}
