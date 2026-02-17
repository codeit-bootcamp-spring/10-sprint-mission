package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Paths;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileBinaryContentRepository extends FileDomainRepository<BinaryContent> implements BinaryContentRepository {
    public FileBinaryContentRepository() throws IOException {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "BinaryContent"),
                ".bc");
    }

    @Override
    public BinaryContent save(BinaryContent entity) throws IOException {
        return save(entity, BinaryContent::getId);
    }
}
