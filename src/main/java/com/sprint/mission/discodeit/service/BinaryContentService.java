package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDto.CreateRequest multipartFileToCreateRequest(MultipartFile file);

    BinaryContentDto.Response create(BinaryContentDto.CreateRequest request);
    BinaryContentDto.Response find(UUID binaryContentId);
    List<BinaryContentDto.Response> findAllByIn(List<UUID> binaryContentIds);
    void delete(UUID binaryContentId);
    BinaryContentDto.Response updateStatus(UUID binaryContentId, BinaryContentStatus status);
}
