package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponseDTO create(BinaryContentCreateRequestDTO req);


    ResponseEntity<BinaryContent> find(UUID id);

    List<BinaryContentResponseDTO> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);

}
