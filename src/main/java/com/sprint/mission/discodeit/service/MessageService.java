package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.FileUploadDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

  PageResponse<MessageDto> findSliceByChannelId(UUID channelId, Pageable pageable);

  MessageDto create(MessageCreateRequest request, List<FileUploadDto> attachments);

  MessageDto update(UUID id, MessageUpdateRequest request);

  void delete(UUID id);
}
