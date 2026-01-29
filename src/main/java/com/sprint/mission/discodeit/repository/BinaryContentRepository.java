package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BinaryContentRepository {
    void save(BinaryContent binaryContent);
    Optional<BinaryContent> findById(String id);
    List<BinaryContent> findAllByIdIn(List<String> ids); //여러 파일 id로 한번에 조회시
    void deleteById(String id);
}
