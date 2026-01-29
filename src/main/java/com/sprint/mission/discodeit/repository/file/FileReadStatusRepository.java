package com.sprint.mission.discodeit.repository.file;

import java.util.UUID;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

// @Repository
public class FileReadStatusRepository implements ReadStatusRepository {
	@Override
	public ReadStatus save(ReadStatus readStatus) {
		return null;
	}

	@Override
	public ReadStatus findByChannelId(UUID channelId) {
		return null;
	}

	@Override
	public void delete(UUID id) {

	}
}
