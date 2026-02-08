package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBinaryContentRepository extends FileDomainRepository<BinaryContent> implements BinaryContentRepository {
    public FileBinaryContentRepository(Path DIRECTORY, String EXTENSION) throws IOException {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "BinaryContent"),
                ".bc");
    }

    @Override
    public BinaryContent save(BinaryContent entity) throws IOException {
        return save(entity, BinaryContent::getId);
    }
}
