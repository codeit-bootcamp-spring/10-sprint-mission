package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.binarycontent.repository.JPABinaryContentRepository;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.common.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.common.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.common.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.MessageDto;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.repository.JPAMessageRepository;
import com.sprint.mission.discodeit.channel.repository.JPAChannelRepository;
import com.sprint.mission.discodeit.paging.dto.PageResponse;
import com.sprint.mission.discodeit.paging.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.JPAUserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;


import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {

  private final JPAMessageRepository jpaMessageRepository;
  private final JPAChannelRepository jpaChannelRepository;
  private final JPAUserRepository jpaUserRepository;
  private final MessageMapper messageMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final JPABinaryContentRepository jpaBinaryContentRepository;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest request,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    log.info("[MESSAGE_CREATE] 메시지 생성 시작 : content={}, channelId={}, authorId={}",
        request.content(), request.channelId(), request.authorId());

    Channel channel = jpaChannelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", request.channelId())));
    User author = jpaUserRepository.findById(request.authorId())
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", request.authorId())));

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(req -> {
          log.info("[MESSAGE_CREATE] 첨부파일 저장 시작 : fileName={}, size={}",
              req.fileName(), req.bytes().length);
          BinaryContent binaryContent = new BinaryContent(
              req.fileName(),
              (long) req.bytes().length,
              req.contentType()
          );
          BinaryContent savedBinaryContent = jpaBinaryContentRepository.save(binaryContent);
          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(savedBinaryContent.getId(), req.bytes())
          );
          log.info("[MESSAGE_CREATE] 첨부파일 저장 완료 : binaryContentId={}",
              savedBinaryContent.getId());
          return savedBinaryContent;
        })
        .toList();

    Message message = new Message(request.content(), channel, author,
        attachments);
    Message savedMessage = jpaMessageRepository.save(message);
    log.info("[MESSAGE_CREATE] 메시지 생성 완료 id={}, channel={}, author={}",
        message.getId(), message.getChannel(), message.getAuthor());

    eventPublisher.publishEvent(
        new MessageCreatedEvent(savedMessage)
    );
    return messageMapper.toDto(savedMessage);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    return jpaMessageRepository.findById(messageId)
        .map(messageMapper::toDto)
        .orElseThrow(
            () -> new MessageNotFoundException(Map.of("messageId", messageId)));
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    Slice<MessageDto> slice = jpaMessageRepository
        .findByChannelIdWithCursor(channelId, cursor, pageable)
        .map(messageMapper::toDto);

    Object nextCursor = slice.hasNext() && !slice.getContent().isEmpty()
        ? slice.getContent().get(slice.getContent().size() - 1).createdAt()
        : null;

    return pageResponseMapper.slice(slice, nextCursor);
  }

  @Override
  @Transactional
  @PreAuthorize("@messageSecurityChecker.isAuthor(#messageId, authentication.principal.userDto.id)")
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    log.info("[MESSAGE_UPDATE] 메시지 수정 시작 : messageId={}, messageNewContent={}",
        messageId, request.newContent());
    Message message = jpaMessageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
    message.update(request.newContent());
    log.info("[MESSAGE_UPDATE] 메시지 수정 완료 : messageId={}, messageNewContent={}",
        messageId, request.newContent());
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  @PreAuthorize("@messageSecurityChecker.isAuthor(#messageId, authentication.principal.userDto.id)")
  public void delete(UUID messageId) {
    log.info("[MESSAGE_DELETE] 메시지 삭제 시작 : messageId={}", messageId);
    Message message = jpaMessageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
    jpaMessageRepository.delete(message);
    log.info("[MESSAGE_DELETE] 메시지 삭제 완료 : messageId={}", messageId);
  }
}
