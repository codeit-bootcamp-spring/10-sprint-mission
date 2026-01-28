package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    //프로필 이미지 업로드 or 메시지에 첨부
    BinaryContent save(BinaryContent binaryContent);
    //파일 다운로드
    Optional<BinaryContent> findById(UUID contentId );
    //모든 이미지 or 파일은.. 관리자용? 테스트용?
    List<BinaryContent> findAll();
    //
    boolean existsById(UUID contentId);
    void deleteById(UUID contentId);
}
