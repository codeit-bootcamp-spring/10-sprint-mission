package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private static final int MESSAGE_PAGE_SIZE = 50;
  private static final Sort MESSAGE_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

    User author = userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(attachment -> new BinaryContent(
            attachment.fileName(),
            (long) attachment.bytes().length,
            attachment.contentType()
        ))
        .toList();

    Message message = new Message(messageCreateRequest.content(), channel, author);
    message.addAttachments(attachments);

    Message savedMessage = messageRepository.save(message);

    for (int i = 0; i < attachments.size(); i++) {
      binaryContentStorage.put(
          attachments.get(i).getId(),
          binaryContentCreateRequests.get(i).bytes()
      );
    }

    return find(savedMessage.getId());
  }

  @Transactional(readOnly = true)
  @Override
  public MessageDto find(UUID messageId) {
    return messageRepository.findWithDetailsById(messageId)
        .map(messageMapper::toDto)
        .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    Pageable normalizedPageable = normalizePageable(pageable);

    Slice<UUID> messageIdSlice = cursor == null
        ? messageRepository.findIdsByChannelId(channelId, normalizedPageable)
        : messageRepository.findIdsByChannelIdBeforeCursor(channelId, cursor, normalizedPageable);

    List<Message> messages = loadMessagesInRequestedOrder(messageIdSlice.getContent());
    List<MessageDto> messageDtoList = messageMapper.toDtoList(messages);

    Instant nextCursor = null;
    if (messageIdSlice.hasNext() && !messages.isEmpty()) {
      nextCursor = messages.get(messages.size() - 1).getCreatedAt();
    }

    Slice<MessageDto> dtoSlice = new SliceImpl<>(
        messageDtoList,
        messageIdSlice.getPageable(),
        messageIdSlice.hasNext()
    );

    return pageResponseMapper.fromSlice(dtoSlice, nextCursor);
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND));

    message.update(messageUpdateRequest.newContent());
    return find(message.getId());
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findWithDetailsById(messageId)
        .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND));

    message.getAttachments().forEach(attachment -> binaryContentStorage.delete(attachment.getId()));
    messageRepository.delete(message);
  }

  private Pageable normalizePageable(Pageable pageable) {
    return PageRequest.of(0, MESSAGE_PAGE_SIZE, MESSAGE_SORT);
  }

  private List<Message> loadMessagesInRequestedOrder(List<UUID> messageIds) {
    if (messageIds.isEmpty()) {
      return List.of();
    }

    Map<UUID, Integer> orderByMessageId = new HashMap<>();
    for (int i = 0; i < messageIds.size(); i++) {
      orderByMessageId.put(messageIds.get(i), i);
    }

    List<Message> messages = new ArrayList<>(
        messageRepository.findAllWithDetailsByIdIn(messageIds)
    );

    messages.sort(Comparator.comparingInt(message -> orderByMessageId.get(message.getId())));
    return messages;
  }
}