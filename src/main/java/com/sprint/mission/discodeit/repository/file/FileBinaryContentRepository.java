package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.utils.FileLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository extends FileRepositoryLockSupport implements BinaryContentRepository {

    private static final Path BINARY_CONTENT_DIRECTORY =
            FileIOHelper.resolveDirectory("binaryContents");

    public FileBinaryContentRepository(FileLockProvider fileLockProvider) {
        super(fileLockProvider);
    }

    @Override
    public List<byte[]> findAllImagesByIds(List<UUID> attachmentIds) {
        return withLock(BINARY_CONTENT_DIRECTORY, () -> attachmentIds.stream()
                .map(id -> BINARY_CONTENT_DIRECTORY.resolve(id.toString()))
                .map(path -> FileIOHelper.<BinaryContent>load(path))
                .flatMap(Optional::stream)
                .map(BinaryContent::getBytes)
                .toList());
    }

    @Override
    public List<BinaryContent> findAllByIds(List<UUID> ids) {
        return withLock(BINARY_CONTENT_DIRECTORY, () -> ids.stream()
                .map(id -> BINARY_CONTENT_DIRECTORY.resolve(id.toString()))
                .map(path -> FileIOHelper.<BinaryContent>load(path))
                .flatMap(Optional::stream)
                .toList());
    }

    @Override
    public void save(BinaryContent binaryContent) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(binaryContent.getId().toString());
        withLock(BINARY_CONTENT_DIRECTORY, () -> FileIOHelper.save(path, binaryContent));
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(id.toString());
        return withLock(BINARY_CONTENT_DIRECTORY, () -> FileIOHelper.load(path));
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(binaryContent.getId().toString());
        withLock(BINARY_CONTENT_DIRECTORY, () -> FileIOHelper.delete(path));
    }

    @Override
    public void deleteById(UUID profileId) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(profileId.toString());
        withLock(BINARY_CONTENT_DIRECTORY, () -> FileIOHelper.delete(path));
    }
}
