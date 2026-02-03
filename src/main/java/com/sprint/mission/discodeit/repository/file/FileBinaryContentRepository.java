package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private static final Path BINARY_CONTENT_DIRECTORY =
            FileIOHelper.resolveDirectory("binaryContents");

    @Override
    public List<byte[]> findImagesByIds(List<UUID> attachmentIds) {
        return attachmentIds.stream()
                .map(id -> BINARY_CONTENT_DIRECTORY.resolve(id.toString()))
                .map(path -> FileIOHelper.<BinaryContent>load(path))
                .flatMap(Optional::stream)
                .map(BinaryContent::getImage)
                .toList();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(binaryContent.getId().toString());
        FileIOHelper.save(path, binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(id.toString());
        return FileIOHelper.load(path);
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(binaryContent.getId().toString());
        FileIOHelper.delete(path);
    }

    @Override
    public void deleteById(UUID profileId) {
        Path path = BINARY_CONTENT_DIRECTORY.resolve(profileId.toString());
        FileIOHelper.delete(path);
    }
}
