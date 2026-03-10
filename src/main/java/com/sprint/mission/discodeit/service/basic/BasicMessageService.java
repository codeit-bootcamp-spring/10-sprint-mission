package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentService binaryContentService;
  private final MessageMapper messageMapper;

  @Override
  public MessageResponse create(MessageCreateRequest req) {
    requireNonNull(req, "request");
    requireNonNull(req.channelId(), "channelId");
    requireNonNull(req.authorId(), "authorId");

    if (req.content() == null || req.content().isBlank()) {
      throw new BusinessLogicException(ErrorCode.MESSAGE_EMPTY);
    }

    Channel channel = channelRepository.findById(req.channelId())
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));

    User user = userRepository.findById(req.authorId())
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    List<UUID> attachmentIds =
        (req.attachmentIds() == null) ? List.of() : List.copyOf(req.attachmentIds());

    List<UUID> distinctIds = attachmentIds.stream()
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    List<BinaryContent> attachments = List.of();
    if (!distinctIds.isEmpty()) {
      attachments = binaryContentRepository.findAllById(distinctIds);

      if (attachments.size() != distinctIds.size()) {
        throw new BusinessLogicException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
      }
    }

    Message saved = messageRepository.save(
        new Message(channel, user, req.content(), attachments)
    );

    return messageMapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<MessageResponse> findAllByChannelId(UUID channelId, Pageable pageable) {
    requireNonNull(channelId, "channelId");
    requireNonNull(pageable, "pageable");

    findChannelOrThrow(channelId);

    Pageable fixedPageable = PageRequest.of(
        Math.max(pageable.getPageNumber(), 0),
        50,
        Sort.by(Sort.Direction.DESC, "createdAt")
    );

    return messageRepository.findByChannel_Id(channelId, fixedPageable)
        .map(messageMapper::toResponse);
  }

  @Override
  public MessageResponse update(MessageUpdateRequest req) {
    requireNonNull(req, "request");
    requireNonNull(req.newMessageId(), "messageId");

    if (req.newContent() == null || req.newContent().isBlank()) {
      throw new BusinessLogicException(ErrorCode.MESSAGE_EMPTY);
    }

    Message message = messageRepository.findById(req.newMessageId())
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND));

    message.updateContent(req.newContent());
    return messageMapper.toResponse(messageRepository.save(message));
  }

  @Override
  public void delete(UUID messageId) {
    requireNonNull(messageId, "messageId");

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.MESSAGE_NOT_FOUND));

    List<UUID> attachmentIds =
        (message.getAttachmentIds() == null) ? List.of() : List.copyOf(message.getAttachmentIds());

    messageRepository.delete(message);

    for (UUID attachmentId : attachmentIds) {
      if (attachmentId != null) {
        binaryContentService.delete(attachmentId);
      }
    }
  }

  private void findChannelOrThrow(UUID channelId) {
    if (channelRepository.findChannel(channelId) == null) {
      throw new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND);
    }
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
    }
  }
}