package com.sprint.mission.discodeit.repository.jcf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
	private final List<ReadStatus> data;

	@Override
	public ReadStatus save(ReadStatus readStatus) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getId().equals(readStatus.getId())) {
				data.set(i, readStatus);
				return readStatus;
			}
		}
		data.add(readStatus);
		return readStatus;
	}

	@Override
	public Optional<ReadStatus> findById(UUID id) {
		return data.stream()
			.filter(readStatus -> readStatus.getId().equals(id))
			.findFirst();
	}

	@Override
	public List<ReadStatus> findByUserId(UUID userId) {
		return data.stream()
			.filter(readStatus -> readStatus.getUserId().equals(userId))
			.collect(Collectors.toList());
	}

	@Override
	public List<ReadStatus> findByChannelId(UUID channelId) {
		return data.stream()
			.filter(readStatus -> readStatus.getChannelId().equals(channelId))
			.collect(Collectors.toList());
	}

	@Override
	public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
		return data.stream()
			.filter(readStatus ->
				readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId)
			)
			.findFirst();
	}

	@Override
	public void delete(UUID id) {
		data.removeIf(readStatus -> readStatus.getId().equals(id));
	}
}
