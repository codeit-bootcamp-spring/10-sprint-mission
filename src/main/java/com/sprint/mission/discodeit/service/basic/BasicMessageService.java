package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.common.InvalidParameterException;
import com.sprint.mission.discodeit.exception.message.MessageEmptyException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageDtoMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentService binaryContentService;
  private final MessageDtoMapper messageDtoMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @Override
  public MessageDto create(MessageCreateRequest req) {
    requireNonNull(req, "request");
    requireNonNull(req.channelId(), "channelId");
    requireNonNull(req.authorId(), "authorId");

    if (req.content() == null || req.content().isBlank()) {
      throw new MessageEmptyException();
    }

    Channel channel = channelRepository.findById(req.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(req.channelId()));

    User user = userRepository.findById(req.authorId())
        .orElseThrow(() -> new UserNotFoundException(req.authorId()));

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
        throw new BinaryContentNotFoundException();
      }
    }

    Message saved = messageRepository.save(
        new Message(channel, user, req.content(), attachments)
    );

    return toDto(saved, Map.of(user.getId(), user));
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
    requireNonNull(channelId, "channelId");

    findChannelOrThrow(channelId);

    int fixedSize = (size <= 0) ? 50 : Math.min(size, 50);
    Pageable pageable = PageRequest.of(0, fixedSize + 1);

    List<UUID> messageIds;
    if (cursor == null) {
      messageIds = messageRepository.findMessageIdsByChannelId(channelId, pageable);
    } else {
      messageIds = messageRepository.findMessageIdsByChannelIdAndCursor(
          channelId,
          cursor,
          pageable
      );
    }

    boolean hasNext = messageIds.size() > fixedSize;
    if (hasNext) {
      messageIds = messageIds.subList(0, fixedSize);
    }

    Long totalElements = messageRepository.countByChannel_Id(channelId);

    if (messageIds.isEmpty()) {
      return new PageResponse<>(List.of(), null, fixedSize, false, totalElements);
    }

    List<Message> messages = messageRepository.findAllByIdInWithAuthorAndAttachments(messageIds);

    Map<UUID, Integer> orderMap = new HashMap<>();
    for (int i = 0; i < messageIds.size(); i++) {
      orderMap.put(messageIds.get(i), i);
    }

    messages = messages.stream()
        .sorted(Comparator.comparingInt(m -> orderMap.get(m.getId())))
        .toList();

    List<UUID> userIds = messages.stream()
        .map(Message::getUserId)
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    Map<UUID, User> userMap = new HashMap<>();
    if (!userIds.isEmpty()) {
      List<User> users = userRepository.findAllByIdInWithProfileImage(userIds);
      for (User user : users) {
        userMap.put(user.getId(), user);
      }
    }

    List<MessageDto> content = messages.stream()
        .map(m -> toDto(m, userMap))
        .toList();

    Object nextCursor = hasNext ? messages.get(messages.size() - 1).getCreatedAt() : null;

    return new PageResponse<>(content, nextCursor, content.size(), hasNext, totalElements);
  }

  @PreAuthorize("@securityExpression.isMessageAuthor(#req.newMessageId())")
  @Override
  public MessageDto update(MessageUpdateRequest req) {
    requireNonNull(req, "request");
    requireNonNull(req.newMessageId(), "messageId");

    if (req.newContent() == null || req.newContent().isBlank()) {
      throw new MessageEmptyException();
    }

    Message message = messageRepository.findById(req.newMessageId())
        .orElseThrow(() -> new MessageNotFoundException(req.newMessageId()));

    message.updateContent(req.newContent());

    return toDto(message, Map.of(message.getUserId(), message.getAuthor()));
  }

  @PreAuthorize("@securityExpression.isMessageAuthor(#messageId)")
  @Override
  public void delete(UUID messageId) {
    requireNonNull(messageId, "messageId");

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId));

    List<UUID> attachmentIds =
        (message.getAttachmentIds() == null) ? List.of() : List.copyOf(message.getAttachmentIds());

    messageRepository.delete(message);

    for (UUID attachmentId : attachmentIds) {
      if (attachmentId != null) {
        binaryContentService.delete(attachmentId);
      }
    }
  }

  private MessageDto toDto(Message message, Map<UUID, User> userMap) {
    UserDto author = null;

    if (message.getUserId() != null) {
      User user = userMap.get(message.getUserId());

      if (user != null) {
        BinaryContentDto profile = null;

        if (user.getProfileImage() != null) {
          profile = binaryContentMapper.toDto(user.getProfileImage());
        }

        boolean online = isOnline(user.getId());
        author = userMapper.toDto(user, online, profile);
      }
    }

    List<BinaryContentDto> attachments = message.getAttachments() == null
        ? List.of()
        : message.getAttachments().stream()
            .map(binaryContentMapper::toDto)
            .toList();

    return messageDtoMapper.toDto(message, author, attachments);
  }

  private boolean isOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .anyMatch(principal -> principal.getUserDto().id().equals(userId));
  }

  private void findChannelOrThrow(UUID channelId) {
    if (channelRepository.findChannel(channelId) == null) {
      throw new ChannelNotFoundException(channelId);
    }
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new InvalidParameterException(name);
    }
  }
}