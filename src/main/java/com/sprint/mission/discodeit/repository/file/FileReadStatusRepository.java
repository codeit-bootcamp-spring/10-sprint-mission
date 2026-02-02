package com.sprint.mission.discodeit.repository.file;

import static com.sprint.mission.discodeit.util.FilePath.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.util.FileIo;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository implements ReadStatusRepository {
	private final FileIo<ReadStatus> fileIo;

	public FileReadStatusRepository() {
		this.fileIo = new FileIo<>(READ_STATUS_DIRECTORY);
	}

	@Override
	public ReadStatus save(ReadStatus readStatus) {
		return fileIo.save(readStatus.getId(), readStatus);
	}

	@Override
	public Optional<ReadStatus> findById(UUID id) {
		return fileIo.load().stream()
			.filter(readStatus -> readStatus.getId().equals(id))
			.findFirst();
	}

	@Override
	public List<ReadStatus> findByUserId(UUID userId) {
		return fileIo.load().stream()
			.filter(readStatus -> readStatus.getUserId().equals(userId))
			.collect(Collectors.toList());
	}

	@Override
	public List<ReadStatus> findByChannelId(UUID channelId) {
		return fileIo.load().stream()
			.filter(readStatus -> readStatus.getChannelId().equals(channelId))
			.collect(Collectors.toList());
	}

	@Override
	public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
		return fileIo.load().stream()
			.filter(readStatus ->
				readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId)
			)
			.findFirst();
	}

	@Override
	public void delete(UUID id) {
		fileIo.delete(id);
	}

}
