package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentUploadException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelParticipantException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicMessageService implements MessageService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final PageMapper pageMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public MessageDto create(MessageCreateRequest request, List<MultipartFile> multipartFiles) {
    User user = userRepository.findById(request.authorId())
        .orElseThrow(() -> new UserNotFoundException(Map.of("authorId", request.authorId())));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", request.channelId())));

    if (channel.getType() == ChannelType.PRIVATE) {
      readStatusRepository.findByUserIdAndChannelId(request.authorId(), request.channelId())
          .orElseThrow(() -> new ChannelParticipantException(Map.of(
              "authorId", request.authorId(), "channelId", request.channelId())));
    }

    Message message = messageMapper.toEntity(request, channel, user);

    //요청에 첨부파일이 있다면 for-loop를 통해 객체 생성 후 저장
    if (multipartFiles != null && !multipartFiles.isEmpty()) {
      for (MultipartFile file : multipartFiles) {
        if (file.isEmpty()) { //리스트 안에 특정 파일이 비었는지 확인
          continue;
        }
        try {
          log.debug("[MESSAGE] 첨부 파일 업로드 시작: name={}, size={}", file.getOriginalFilename(),
              file.getSize());
          BinaryContent attachment = new BinaryContent(
              file.getOriginalFilename(),
              file.getSize(),
              file.getContentType()
          );
          binaryContentRepository.save(attachment);
          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(attachment.getId(), file.getBytes()));
          message.addAttachment(attachment); //편의 메서드 사용
          log.info("[MESSAGE] 첨부 파일 저장 성공: attachmentId={}", attachment.getId());
        } catch (IOException e) {
          throw new BinaryContentUploadException(e);
        }
      }
    }
    messageRepository.save(message);
    eventPublisher.publishEvent(
        new MessageCreatedEvent(message.getId(), channel.getId(), user.getId(), user.getUsername(),
            channel.getName(), message.getContent()));
    log.info("[MESSAGE] 메시지 생성 완료: messageId={}", message.getId());
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto findById(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
    log.debug("[MESSAGE] 메시지 조회 완료: messageId={}, content={}", message.getId(),
        message.getContent());
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    if (!channelRepository.existsById(channelId)) {
      throw new ChannelNotFoundException(Map.of("channelId", channelId));
    }
    Slice<MessageDto> slice = messageRepository.findAllByChannelIdAndCreatedAtLessThan(channelId,
            Optional.ofNullable(cursor).orElse(Instant.now()),
            pageable)
        .map(messageMapper::toDto);
    Instant nextCursor = null;
    if (!slice.getContent().isEmpty() && slice.hasNext()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }
    log.debug("[MESSAGE] 메시지 목록 조회 완료: channelId={}, count={}, hasNext={}, nextCursor={}",
        channelId, slice.getContent().size(), slice.hasNext(), nextCursor);
    return pageMapper.fromSlice(slice, nextCursor);
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
    message.update(request.newContent());
    log.info("[MESSAGE] 메시지 수정 완료: messageId={}", message.getId());
    return messageMapper.toDto(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
    messageRepository.delete(message);
    log.info("[MESSAGE] 메시지 삭제 완료: messageId={}", message.getId());
  }

  @Transactional(readOnly = true)
  public boolean isMessageAuthor(UUID messageId, UUID userId) {
    return messageRepository.findById(messageId)
        .map(Message::getAuthor)
        .map(author -> author.getId().equals(userId))
        .orElse(false);
  }
}
