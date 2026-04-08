package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageAuthorNotInChannelException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final ReadStatusRepository readStatusRepository;

  @Override
  @Transactional
  public MessageDto createMessage(MessageCreateRequest request, List<MultipartFile> files) {
    log.debug("Message creation requested: channel={}, author={}", request.getChannelId(), request.getAuthorId());
    Channel channel = channelRepository.findById(request.getChannelId())
        .orElseThrow(() -> {
          log.warn("Message creation failed - Channel not found: id={}", request.getChannelId());
          return new ChannelNotFoundException(request.getChannelId());
        });
    User user = userRepository.findById(request.getAuthorId())
        .orElseThrow(() -> {
          log.warn("Message creation failed - User not found: id={}", request.getAuthorId());
          return new UserNotFoundException(request.getAuthorId());
        });

    if (readStatusRepository.findByUser_IdAndChannel_Id(user.getId(), channel.getId()).isEmpty()) {
      if (ChannelType.PUBLIC.equals(channel.getType())) {
        readStatusRepository.save(new ReadStatus(user, channel));
      } else {
        log.warn("Message creation failed - User not in channel: userId={}, channelId={}", user.getId(), channel.getId());
        throw new MessageAuthorNotInChannelException(user.getId(), channel.getId());
      }
    }

    List<BinaryContent> attachments = saveBinaryContents(files);

    Message message = new Message(channel, user, request.getContent());
    attachments.forEach(message::addAttachment);

    messageRepository.save(message);
    log.info("Message created successfully: id={}, channelId={}, authorId={}", message.getId(), channel.getId(), user.getId());

    return messageMapper.toDto(message);
  }

  @Override
  public MessageDto getMessage(UUID id) {
    log.debug("Fetching message details: id={}", id);
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Message not found: id={}", id);
          return new MessageNotFoundException(id);
        });
    return messageMapper.toDto(message);
  }

  @Override
  public List<MessageDto> getAllMessages() {
    log.debug("Fetching all messages");
    return messageRepository.findAll().stream()
        .map(messageMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public Slice<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
    log.debug("Fetching messages for channel: channelId={}, cursor={}, size={}", channelId, cursor, size);
    Pageable pageable = PageRequest.of(0, size, Sort.by("createdAt").descending());
    if (cursor != null) {
      return messageRepository.findByChannel_IdAndCreatedAtBefore(channelId, cursor, pageable)
          .map(messageMapper::toDto);
    }
    return messageRepository.findByChannel_Id(channelId, pageable)
        .map(messageMapper::toDto);
  }

  @Override
  @Transactional
  public MessageDto updateMessage(UUID messageId, MessageUpdateRequest request) {
    log.debug("Message update requested: id={}", messageId);
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("Update failed - Message not found: id={}", messageId);
          return new MessageNotFoundException(messageId);
        });

    if (request.getNewContent() != null && !request.getNewContent().isBlank()) {
      message.updateContent(request.getNewContent());
    }

    log.info("Message updated successfully: id={}", messageId);
    return messageMapper.toDto(message);
  }

  @Transactional
  protected List<BinaryContent> saveBinaryContents(List<MultipartFile> files) {
    List<BinaryContent> attachments = new ArrayList<>();
    if (files != null) {
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          try {
            BinaryContent binaryContent = new BinaryContent(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
            binaryContentStorage.put(binaryContent.getId(), file.getBytes());
            attachments.add(binaryContent);
            log.debug("Attachment saved: id={}, filename={}", binaryContent.getId(), file.getOriginalFilename());
          } catch (IOException e) {
            log.error("Failed to save attachment", e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
          }
        }
      }
    }
    return attachments;
  }

  @Override
  @Transactional
  public void deleteMessage(UUID id) {
    log.debug("Message deletion requested: id={}", id);
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Deletion failed - Message not found: id={}", id);
          return new MessageNotFoundException(id);
        });

    for (BinaryContent attachment : new ArrayList<>(message.getAttachments())) {
      binaryContentRepository.delete(attachment);
    }

    messageRepository.delete(message);
    log.info("Message deleted successfully: id={}", id);
  }

  @Override
  public List<MessageDto> getMessagesByUserId(UUID userId) {
    return messageRepository.findAllByAuthor_Id(userId).stream()
        .map(messageMapper::toDto)
        .collect(Collectors.toList());
  }
}
