package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

    // Create
    MessageDto create(MessageCreateRequest messageCreateRequest, List<MultipartFile> attachments)
            throws IOException;

    // Read
    MessageDto findById(UUID id);

    // ReadAll
    PageResponse<MessageDto> findAllByChannelId(UUID userId, UUID channelId, UUID cursor,
            Pageable pageable);

    // Update
    @PreAuthorize("@messageSecurityService.isAuthor(#id, authentication.principal.userDto.id)")
    MessageDto update(UUID id, MessageUpdateRequest messageUpdateRequest);


    PageResponse<MessageDto> getMessages(UUID channelId, Instant cursor, Pageable pageable);

    // Delete
    @PreAuthorize("@messageSecurityService.isAuthor(#id, authentication.principal.userDto.id)")
    void delete(UUID id);

}
