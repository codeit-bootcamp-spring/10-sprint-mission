package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MessageApi {

    ResponseEntity<MessageDto> create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments);

    ResponseEntity<MessageDto> update(UUID messageId, MessageUpdateRequest request);

    ResponseEntity<Void> delete(UUID messageId);

    ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(
            UUID channelId,
            Instant cursorCreatedAt,
            UUID cursorId,
            Integer size
    );
}