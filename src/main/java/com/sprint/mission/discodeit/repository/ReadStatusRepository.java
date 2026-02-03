package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.ReadStatus;

public interface ReadStatusRepository {
	ReadStatus save(ReadStatus readStatus);

	Optional<ReadStatus> findById(UUID id);

	List<ReadStatus> findByUserId(UUID userId);

	List<ReadStatus> findByChannelId(UUID channelId);

	Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

	void delete(UUID id);
}
