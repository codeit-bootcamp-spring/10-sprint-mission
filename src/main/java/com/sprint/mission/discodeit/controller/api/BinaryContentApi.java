package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentApi {

    ResponseEntity<BinaryContentDto> find(UUID binaryContentId);

    ResponseEntity<List<BinaryContentDto>> findAllByIdIn(List<UUID> binaryContentIds);

    ResponseEntity<?> download(UUID binaryContentId);
}