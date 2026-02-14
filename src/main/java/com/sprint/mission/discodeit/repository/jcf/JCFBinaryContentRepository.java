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
@ConditionalOnProperty(
        name = "discodeit.repository.type" ,
        havingValue = "jcf" ,
        matchIfMissing = true
)
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private  final List<BinaryContent> data;

    public JCFBinaryContentRepository() {
        data = new ArrayList<>();
    }

    // 첨부 파일 저장
    @Override
    public void save(BinaryContent binaryContent) {
        data.removeIf(existgBinaryContent -> existgBinaryContent.getId().equals(binaryContent.getId()));

        data.add(binaryContent);
    }

    // 첨부 파일 단건 조회
    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        return data.stream()
                .filter(binaryContent -> binaryContent.getId().equals(binaryContentId))
                .findFirst();
    }

    // 첨부 파일 전체 조회
    @Override
    public List<BinaryContent> findAll() {
        return data;
    }

    // 첨부 파일 삭제
    @Override
    public void delete(BinaryContent binaryContent) {
        data.remove(binaryContent);
    }
}
