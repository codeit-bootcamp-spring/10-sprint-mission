package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data;

    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<BinaryContent> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(bc -> bc.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findByMessageId(UUID messageId) {
        return data.values().stream()
                .filter(bc -> bc.getMessageId().equals(messageId))
                .toList();
    }

    @Override
    public List<UUID> findAll() {
        return new ArrayList<>(data.keySet());
    }

    @Override
    public void save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(),binaryContent);
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.values().removeIf(bc -> bc.getUserId().equals(userId));
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        data.values().removeIf(bc -> bc.getMessageId().equals(messageId));
    }
}
