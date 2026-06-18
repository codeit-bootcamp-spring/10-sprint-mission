package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Transactional
  @Override
  @CacheEvict(value = "userChannels", allEntries = true)
  public MessageDto create(MessageCreateRequest request,
      List<MultipartFile> attachments) {
    User author = userRepository.findById(request.getAuthorId())
        .orElseThrow(() -> new UserNotFoundException(request.getAuthorId()));
    Channel channel = channelRepository.findById(request.getChannelId())
        .orElseThrow(() -> new ChannelNotFoundException(request.getChannelId()));

    List<BinaryContent> attachmentContents = processAttachments(attachments);

    Message message = new Message(
        author,
        channel,
        request.getContent(),
        attachmentContents
    );

    Message saved = messageRepository.saveAndFlush(message);

    eventPublisher.publishEvent(new MessageCreatedEvent(saved.getId()));

    log.info("[SUCCESS] Created Message: id={}, channelId={}",
        saved.getId(), channel.getId());

    return messageMapper.toDto(saved);
  }

  @Override
  public MessageDto findById(UUID id) {
    return messageMapper.toDto(findMessageEntityById(id));
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));

    // 1. Repository 조회 (hasNext 판단을 위해 size + 1개 요청)
    Pageable limit = PageRequest.of(0, size + 1);
    List<Message> entities = (cursor == null)
        ? messageRepository.findByChannelOrderByCreatedAtDesc(channel, limit)
        : messageRepository.findByChannelAndCursor(channel, cursor, limit);

    // 2. DTO 변환
    List<MessageDto> dtos = entities.stream().map(messageMapper::toDto).toList();

    // 3. PageResponseMapper를 통한 응답 객체 생성
    return pageResponseMapper.toCursorPageResponse(
        dtos,
        size,
        MessageDto::getCreatedAt,
        messageRepository.countByChannel(channel)
    );
  }

  @Transactional
  @Override
  @PreAuthorize("@messageRepository.findWithAuthorAndAttachmentsById(#messageId).orElse(null)?.author?.id == authentication.principal.userDto.id")
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = findMessageEntityById(messageId);

    message.update(request.getNewContent());

    log.info("[SUCCESS] Updated Message: id={}", messageId);

    return messageMapper.toDto(message);
  }

  @Transactional
  @Override
  @CacheEvict(value = "userChannels", allEntries = true)
  @PreAuthorize("@messageRepository.findWithAuthorAndAttachmentsById(#messageId).orElse(null)?.author?.id == authentication.principal.userDto.id")
  public void delete(UUID messageId) {
    Message message = findMessageEntityById(messageId);

    messageRepository.delete(message);

    log.info("[SUCCESS] Deleted Message: id={}", messageId);
  }

  // [헬퍼 메서드]: 첨부 파일 처리
  private List<BinaryContent> processAttachments(List<MultipartFile> attachments) {
    List<BinaryContent> results = new ArrayList<>();
    if (attachments == null || attachments.isEmpty()) {
      return results;
    }

    for (MultipartFile file : attachments) {
      try {
        BinaryContent content = new BinaryContent(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType()
        );
        BinaryContent saved = binaryContentRepository.save(content);

        eventPublisher.publishEvent(
            new BinaryContentCreatedEvent(
                saved.getId(),
                file.getBytes())
        );

        results.add(saved);

      } catch (IOException e) {
        throw new DiscodeitException(ErrorCode.FILE_SAVE_ERROR);
      }
    }
    return results;
  }

  // [헬퍼 메서드]: 반복되는 조회 및 예외 처리 공통화
  private Message findMessageEntityById(UUID id) {
    return messageRepository.findWithAuthorAndAttachmentsById(id)
        .orElseThrow(() -> new MessageNotFoundException(id));
  }
}
