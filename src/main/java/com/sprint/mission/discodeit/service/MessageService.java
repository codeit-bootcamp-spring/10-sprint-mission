package com.sprint.mission.discodeit.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;

public interface MessageService {

	MessageDto create(MessageCreateRequest messageCreateRequest,
		List<BinaryContentCreateRequest> binaryContentCreateRequests);

	MessageDto find(UUID messageId);

	PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant createdAt,
		Pageable pageable);

	@PreAuthorize("@resourceAuthorizationService.canModifyMessage(authentication, #messageId)")
	MessageDto update(@Param("messageId") UUID messageId, MessageUpdateRequest request);

	@PreAuthorize("@resourceAuthorizationService.canModifyMessage(authentication, #messageId)")
	void delete(@Param("messageId") UUID messageId);
}
