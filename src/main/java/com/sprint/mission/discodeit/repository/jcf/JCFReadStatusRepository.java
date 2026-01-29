package com.sprint.mission.discodeit.repository.jcf;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
	@Override
	public ReadStatus save(ReadStatus readStatus) {
		return null;
	}

	@Override
	public void delete(UUID id) {

	}
}
