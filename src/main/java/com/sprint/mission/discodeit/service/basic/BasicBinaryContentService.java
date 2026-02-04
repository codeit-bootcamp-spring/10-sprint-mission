package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContent content) {
        requireNonNull(content, "content");

        return binaryContentRepository.save(content);
    }

    @Override
    public BinaryContent find(UUID id) {
        requireNonNull(id, "id");

        BinaryContent found = binaryContentRepository.findById(id);
        if (found == null) {
            throw new IllegalArgumentException("BinaryContent not found: " + id);
        }
        return found;
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        requireNonNull(ids, "ids");
        return binaryContentRepository.findAllByIdIn(ids);
    }

    @Override
    public void delete(UUID id) {
        requireNonNull(id, "id");
        binaryContentRepository.delete(id);
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}
