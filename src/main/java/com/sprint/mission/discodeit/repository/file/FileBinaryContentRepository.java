package com.sprint.mission.discodeit.repository.file;

import static com.sprint.mission.discodeit.util.FilePath.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.util.FileIo;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {
	private final FileIo<BinaryContent> fileIo;

	public FileBinaryContentRepository() {
		this.fileIo = new FileIo<>(BINARY_CONTENT_DIRECTORY);
	}

	@Override
	public BinaryContent save(BinaryContent binaryContent) {
		return fileIo.save(binaryContent.getId(), binaryContent);
	}

	@Override
	public Optional<BinaryContent> findById(UUID id) {
		return fileIo.load().stream()
			.filter(binaryContent -> binaryContent.getId().equals(id))
			.findFirst();
	}

	@Override
	public List<BinaryContent> findByIdIn(List<UUID> ids) {
		return fileIo.load().stream()
			.filter(binaryContent -> ids.contains(binaryContent.getId()))
			.collect(Collectors.toList());
	}

	@Override
	public void delete(UUID id) {
		fileIo.delete(id);
	}
}
