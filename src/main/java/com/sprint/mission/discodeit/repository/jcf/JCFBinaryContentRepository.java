package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final HashMap<UUID, BinaryContent> data;

    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        this.data.put(binaryContent.getId(), binaryContent);

        return binaryContent;
    }

    @Override
    public void deleteByID(UUID id) {
        this.data.remove(id);
    }

    @Override
    public Optional<BinaryContent> findbyId(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return this.data.values().stream().toList();
    }
}
