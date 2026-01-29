package com.sprint.mission.discodeit.repository;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.ReadStatus;

public interface ReadStatusRepository {
	ReadStatus save(ReadStatus readStatus);

	ReadStatus findByChannelId(UUID channelId);

	void delete(UUID id);
}
