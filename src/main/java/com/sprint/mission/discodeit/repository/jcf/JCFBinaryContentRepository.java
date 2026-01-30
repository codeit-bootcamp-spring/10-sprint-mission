package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final List<BinaryContent> data = new ArrayList<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        delete(binaryContent);
        data.add(binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        return data.stream()
                .filter(content -> content.getId().equals(binaryContentId))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void delete(BinaryContent binaryContent) {
        data.removeIf(b -> b.equals(binaryContent));
    }
}
