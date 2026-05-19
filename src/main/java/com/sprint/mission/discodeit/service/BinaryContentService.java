package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;

public interface BinaryContentService {

	BinaryContentDto create(BinaryContentCreateRequest request);

	BinaryContentDto find(UUID binaryContentId);

	List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

	void delete(UUID binaryContentId);
}
