package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;

  @Override
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));
    User author = userRepository.findById(authorId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + authorId + " does not exist"));

    List<BinaryContentCreateRequest> attachmentRequests =
            (binaryContentCreateRequests == null) ? Collections.emptyList() : binaryContentCreateRequests;

    // attachments 저장 -> DB에는 메타, bytes는 storage에.
    List<BinaryContent> attachments = attachmentRequests.stream()
            .map(binaryContentService::create)
            .toList();

    // 이제 생성자 다시 객체로 받음.
    Message message = new Message(
        messageCreateRequest.content(),
        channel,
        author,
        attachments
    );
    return messageRepository.save(message);
  }

  @Transactional(readOnly = true)
  @Override
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Transactional(readOnly = true)
  @Override
  public List<Message> findAllByChannel_Id(UUID channelId, Instant cursor, int size) {
    PageRequest pageRequest = PageRequest.of(0, size + 1);
    if (cursor == null) {
      return messageRepository.findByChannel_IdOrderByCreatedAtDesc(channelId, pageRequest);
    }

    return messageRepository.findByChannel_IdAndCreatedAtBeforeOrderByCreatedAtDesc(
            channelId, cursor, pageRequest
    );
  }


  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(request.newContent());
    // 이제 변경시 알아서 감지 후 저장.
    return message;
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    messageRepository.delete(message);
  }
}
