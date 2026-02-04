package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.IntStream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final List<BinaryContent> data;

    public JCFBinaryContentRepository() {
        this.data = new ArrayList<>();
    }
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(binaryContent.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            throw new IllegalStateException("BinaryContent는 수정할 수 없습니다.");
        } else {
            data.add(binaryContent);
        }

        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return data.stream()
                .filter(bc -> bc.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data);

    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(bs -> bs.getId().equals(id));
    }
}
