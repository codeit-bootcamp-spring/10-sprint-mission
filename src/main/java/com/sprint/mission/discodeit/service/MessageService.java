package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;


public interface MessageService {

  Message create(String content, UUID authorId, UUID channelId, List<MultipartFile> attachments);

  Message findById(UUID id);

  Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  Message update(UUID id, String newContent, List<UUID> attachmentIds);

  void deleteById(UUID id);
}