package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageCursor;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(
          MessageCreateRequest messageCreateRequest,
          List<BinaryContentCreateRequest> attachmentRequests
  ) {
    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
            .orElseThrow(() -> new NoSuchElementException(
                    "Channel with id " + messageCreateRequest.channelId() + " not found"
            ));

    User author = userRepository.findById(messageCreateRequest.authorId())
            .orElseThrow(() -> new NoSuchElementException(
                    "Author with id " + messageCreateRequest.authorId() + " not found"
            ));

    List<BinaryContent> attachments = attachmentRequests.stream()
            .map(this::storeBinaryContent)
            .toList();

    Message saved = messageRepository.save(
            new Message(messageCreateRequest.content(), channel, author, attachments)
    );

    return messageMapper.toDto(saved);
  }

  @Override
  public MessageDto find(UUID messageId) {
    return messageRepository.findWithDetailsById(messageId)
            .map(messageMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(
          UUID channelId,
          Instant cursorCreatedAt,
          UUID cursorId,
          Integer size
  ) {
    if ((cursorCreatedAt == null) != (cursorId == null)) {
      throw new IllegalArgumentException("cursorCreatedAt and cursorId must be provided together");
    }

    int pageSize = (size == null || size <= 0) ? 50 : Math.min(size, 100);

    Pageable pageable = PageRequest.of(0, pageSize + 1);

    List<Message> messages = messageRepository.findCursorPageByChannelId(
            channelId,
            cursorCreatedAt,
            cursorId,
            pageable
    );

    boolean hasNext = messages.size() > pageSize;
    List<Message> pageMessages = hasNext ? messages.subList(0, pageSize) : messages;

    List<MessageDto> content = pageMessages.stream()
            .map(messageMapper::toDto)
            .toList();

    MessageCursor nextCursor = null;
    if (hasNext && !pageMessages.isEmpty()) {
      Message lastMessage = pageMessages.get(pageMessages.size() - 1);
      nextCursor = new MessageCursor(lastMessage.getCreatedAt(), lastMessage.getId());
    }

    return pageResponseMapper.fromCursor(content, nextCursor, pageSize, hasNext);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

    message.update(request.newContent(), null);
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    if (!messageRepository.existsById(messageId)) {
      throw new NoSuchElementException("Message with id " + messageId + " not found");
    }
    messageRepository.deleteById(messageId);
  }

  private BinaryContent storeBinaryContent(BinaryContentCreateRequest request) {
    BinaryContent binaryContent = new BinaryContent(
            request.fileName(),
            (long) request.bytes().length,
            request.contentType()
    );
    BinaryContent saved = binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(saved.getId(), request.bytes());
    return saved;
  }
}