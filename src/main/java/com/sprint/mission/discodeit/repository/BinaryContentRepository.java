package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    // 첨부파일 저장
    void save(BinaryContent binaryContent);

    // 첨부파일 단건 조회
    Optional<BinaryContent> findById(UUID binaryContentId);

    // 첨부파일 전체 조회
    List<BinaryContent> findAll();

    // 첨부파일 삭제
    void delete(BinaryContent binaryContent);
}
