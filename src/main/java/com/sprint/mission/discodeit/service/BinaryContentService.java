package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentService {

    UUID createBinaryContent(BinaryContentRequest request);

    List<BinaryContent> createBinaryContents(List<BinaryContentRequest> requests);

    BinaryContentDto findBinaryContent(UUID binaryContentId);

    BinaryContent findBinaryContentEntity(UUID binaryContentId);

    List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

    ResponseEntity<?> downloadBinaryContent(UUID binaryContentId);

    void deleteBinaryContent(UUID binaryContentId);
}
