package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments);

  MessageDto findById(UUID id);

  PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size);

  @PreAuthorize("@messageRepository.findWithAuthorAndAttachmentsById(#root.args[0]).orElse(null)?.author?.id == authentication.principal.userDto.id")
  MessageDto update(UUID messageId, MessageUpdateRequest request);

  @PreAuthorize("@messageRepository.findWithAuthorAndAttachmentsById(#root.args[0]).orElse(null)?.author?.id == authentication.principal.userDto.id")
  void delete(UUID id);
}
