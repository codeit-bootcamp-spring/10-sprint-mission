package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent){
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAllById(Iterable<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent content = data.get(id);
            if (content != null) {
                result.add(content);
            }
        }
        return result;
    }

    @Override
    public List<BinaryContent> findAll(){
        return data.values().stream().toList();
    }

    @Override
    public void deleteById(UUID id){
        data.remove(id);
    }
}
