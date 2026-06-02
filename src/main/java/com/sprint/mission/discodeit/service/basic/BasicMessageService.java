package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.*;
import com.sprint.mission.discodeit.exception.channel.*;
import com.sprint.mission.discodeit.exception.message.*;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public Message create(String content, UUID authorId, UUID channelId,
      List<MultipartFile> attachments) {
    log.info("Creating new message in channel ID: {} by author ID: {}", channelId,
        authorId); // 메시지 생성 시작 로그

    User author = getOrThrowUser(authorId);
    Channel channel = getOrThrowChannel(channelId);
    validateAccess(authorId, channel);

    List<BinaryContent> binaryContents = new ArrayList<>();
    if (attachments != null && !attachments.isEmpty()) {
      log.debug("Processing {} attachments for new message",
          attachments.size()); // 첨부파일 처리 개수 디버그 로그

      attachments.stream()
          .filter(file -> !file.isEmpty())
          .forEach(file -> {
            try {
              BinaryContent binaryContent = new BinaryContent(
                  file.getOriginalFilename(),
                  file.getSize(),
                  file.getContentType()
              );
              binaryContentRepository.save(binaryContent); // 메타데이터 저장

              // 메타데이터 저장 이벤트 발행
              eventPublisher.publishEvent(
                  new BinaryContentCreatedEvent(binaryContent.getId(), file.getBytes()));

              binaryContents.add(binaryContent);
              log.debug("Attachment saved successfully: {}",
                  file.getOriginalFilename()); // 단일 파일 저장 성공 로그
            } catch (IOException e) {
              throw new FileUploadException(Map.of(
                  "authorId", authorId,
                  "channelId", channelId,
                  "fileName",
                  Objects.requireNonNullElse(file.getOriginalFilename(), "unknown")
              ), e);
            }
          });
    }

    Message newMessage = new Message(content, author, channel, binaryContents);
    Message savedMessage = messageRepository.save(newMessage);

    log.info("Message created successfully. ID: {}", savedMessage.getId()); // 메시지 생성 성공 로그
    return getOrThrowMessage(savedMessage.getId());
  }

  @Override
  public Message findById(UUID id) {
    log.debug("Fetching message by ID: {}", id); // 메시지 단건 조회 시작 로그

    Message message = getOrThrowMessage(id);

    log.debug("Message found: {}", message.getId()); // 메시지 단건 조회 성공 로
    return message;
  }

  @Override
  public Slice<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {
    log.info("Fetching messages for channel ID: {} with cursor: {}", channelId,
        cursor); // 메시지 목록 조회 시작 로그

    // 커서가 없을 시, 현재가 기준 (최신 메시지부터 조회)
    Instant targetTime = Optional.ofNullable(cursor).orElse(Instant.now());

    // 메시지와 작성자 정보 가져옴
    Slice<Message> messageSlice = messageRepository.findAllByChannelIdWithAuthor(
        channelId, targetTime, pageable);

    // 트랜잭션이 끝나기 전 컬렉션 호출 -> 설정된 Batch Size에 따라 메시지별 첨부파일을 순회하며 조회
    messageSlice.getContent().forEach(message -> {
      if (message.getAttachments() != null) {
        message.getAttachments().forEach(attachment -> attachment.getId());
      }
    });

    log.debug("Fetched {} messages for channel ID: {}", messageSlice.getNumberOfElements(),
        channelId); // 조회된 메시지 개수 로그
    return messageSlice;
  }

  @Override
  @Transactional
  // SpEL을 활용해 커스텀 검증기를 호출하여 메시지 작성자와 현재 로그인한 사람이 일치할 때만 실행
  @PreAuthorize("@messageSecurityValidator.isAuthor(#id, authentication.principal.id)")
  public Message update(UUID id, UUID requesterId, String newContent) {
    log.info("Updating message with ID: {}", id); // 메시지 수정 시작 로그

    Message message = getOrThrowMessage(id);

    // 텍스트 내용만 수정
    Optional.ofNullable(newContent).ifPresent(content -> {
      log.debug("Changing content for message ID: {}", id); // 메시지 수정 로그
      message.updateContent(content);
    });

    log.info("Message ID {} updated successfully", id); // 메시지 수정 성공 로그
    return message;
  }

  @Override
  @Transactional
  // SpEL을 활용해 커스텀 검증기를 호출하여 메시지 작성자와 현재 로그인한 사람이 일치할 때만 실행
  @PreAuthorize("@messageSecurityValidator.isAuthor(#id, authentication.principal.id)")
  public void deleteById(UUID id, UUID requesterId) {
    log.info("Deleting message with ID: {}", id); // 메시지 삭제 시작 로그

    Message message = getOrThrowMessage(id);
    messageRepository.delete(message);

    log.info("Message ID {} deleted successfully", id); // 메시지 삭제 성공 로그
  }

  // --- Helper Methods ---

  // 비공개 채널 접근 권한 확인
  private void validateAccess(UUID userId, Channel channel) {
    if (channel.getType() == ChannelType.PRIVATE) {
      readStatusRepository.findByUserIdAndChannelId(userId, channel.getId())
          .orElseThrow(() -> new ChannelAccessDeniedException(Map.of(
              "userId", userId,
              "channelId", channel.getId(),
              "reason", "PRIVATE 채널에 참여 중이지 않은 유저입니다."
          )));
    }
  }

  // 유저 검증
  private User getOrThrowUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(Map.of("requestedUserId", id)));
  }

  // 채널 검증
  private Channel getOrThrowChannel(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("requestedChannelId", id)));
  }

  // 메시지 검증
  private Message getOrThrowMessage(UUID id) {
    return messageRepository.findByIdWithDetails(id)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("requestedMessageId", id)));
  }
}
