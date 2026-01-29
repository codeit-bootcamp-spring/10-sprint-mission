package com.sprint.mission.discodeit.repository;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.ReadStatus;

public interface ReadStatusRepository {
	ReadStatus save(ReadStatus readStatus);

	void delete(UUID id);
}
