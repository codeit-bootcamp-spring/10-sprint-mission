package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
@Primary
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public void save(BinaryContent binaryContent) {
        if(binaryContent == null) throw new IllegalArgumentException("binaryContent는 null일 수 없습니다.");
        if(binaryContent.getId() == null) throw new IllegalArgumentException("binaryContent.id는 null일 우 없습니다.");

        //같은 id가 이미 있으먄
        data.put(binaryContent.getId(), binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if(id==null) {
            throw new IllegalArgumentException("id는 null일 수 없습니다.");
        }
        // 없으면 null -> Optional.empty()
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        if(id==null) {
            throw new IllegalArgumentException("id는 null일 수 없습니다.");
        }
        data.remove(id);
    }
}
