package com.sprint.mission.discodeit.repository.file;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.util.FileIo;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {
	private final FileIo<UserStatus> fileIo;

	public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String directory) {
		this.fileIo = new FileIo<>(Paths.get(directory).resolve(UserStatus.class.getSimpleName().toLowerCase()));
	}

	@Override
	public UserStatus save(UserStatus userStatus) {
		return fileIo.save(userStatus.getId(), userStatus);
	}

	@Override
	public Optional<UserStatus> findById(UUID id) {
		return fileIo.load().stream()
			.filter(userStatus -> userStatus.getId().equals(id))
			.findFirst();
	}

	@Override
	public Optional<UserStatus> findByUserId(UUID userId) {
		return fileIo.load().stream()
			.filter(userStatus -> userStatus.getUserId().equals(userId))
			.findFirst();
	}

	@Override
	public List<UserStatus> findAll() {
		return fileIo.load();
	}

	@Override
	public void delete(UUID id) {
		fileIo.delete(id);
	}
}
