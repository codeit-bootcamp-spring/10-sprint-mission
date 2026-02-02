package com.sprint.mission.discodeit.repository.jcf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFBinaryContentRepository implements BinaryContentRepository {
	private final List<BinaryContent> data;

	@Override
	public BinaryContent save(BinaryContent binaryContent) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getId().equals(binaryContent)) {
				data.set(i, binaryContent);
				return binaryContent;
			}
		}

		data.add(binaryContent);
		return binaryContent;
	}

	@Override
	public Optional<BinaryContent> findById(UUID id) {
		return data.stream()
			.filter(binaryContent -> binaryContent.getId().equals(id))
			.findFirst();
	}

	@Override
	public List<BinaryContent> findByIdIn(List<UUID> ids) {
		return data.stream()
			.filter(binaryContent -> ids.contains(binaryContent.getId()))
			.collect(Collectors.toList());
	}

	@Override
	public void delete(UUID id) {
		data.removeIf(binaryContent -> binaryContent.getId().equals(id));
	}
}