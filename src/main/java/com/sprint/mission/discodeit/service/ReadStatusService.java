package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatusdto.RSCreateRequestDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.RSResponseDTO;
import com.sprint.mission.discodeit.dto.readstatusdto.RSUpdateRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    RSResponseDTO create(RSCreateRequestDTO req);
    RSResponseDTO find(UUID rsId);
    List<RSResponseDTO> findAllByUserId(UUID userId);
    RSResponseDTO update(RSUpdateRequestDTO req);
    void delete(UUID id);



}
