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
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final List<BinaryContent> data = new ArrayList<>();

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return data.stream()
                .filter(bc -> bc.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void save(BinaryContent binaryContent) {
        if(data.contains(binaryContent))
            data.set(data.indexOf(binaryContent), binaryContent);
        else
            data.add(binaryContent);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(bc -> bc.getId().equals(id));
    }
}
