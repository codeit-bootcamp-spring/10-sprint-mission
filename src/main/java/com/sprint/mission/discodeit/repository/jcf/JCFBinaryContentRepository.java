package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data  = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        this.data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID uuid) {
        return Optional.ofNullable(this.data.get(uuid));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> uuids) {
        return uuids.stream()
                .map(this.data::get)
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        this.data.remove(uuid);
    }
}
