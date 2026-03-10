package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

    MessageDto create(List<MultipartFile> profile, MessageCreateRequestDTO req);

    MessageDto find(UUID messageId);

    List<MessageDto> findAllByChannelId(UUID channelId);

    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);

    MessageDto update(UUID messageId, MessageUpdateRequestDto req);

    void delete(UUID messageId);
}
