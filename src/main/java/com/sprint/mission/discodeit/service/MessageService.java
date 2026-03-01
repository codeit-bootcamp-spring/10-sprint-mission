package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.Message;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import java.util.UUID;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;


public interface MessageService {

  Message create(MessageCreateRequest request, MultipartFile[] attachments);

  Message findById(UUID id);

  List<Message> findAllByChannelId(UUID channelId); // 특정 채널의 메시지 목록 조회

  Message update(UUID id, MessageUpdateRequest request);

  void deleteById(UUID id);

  Message togglePin(UUID id); // 메시지 고정
}