package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentService binaryContentService;

  @Override
  public Message create(
      MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    log.debug("메세지 생성 요청- channelId={}, authorId={}", channelId, authorId);

    Channel channel =
        channelRepository
            .findById(channelId)
            .orElseThrow(
                () -> {
                  log.warn("메세지 생성 실패 - 채널 없음 channelId={}, authorId={}", channelId, authorId);
                  return new ChannelNotFoundException(channelId);
                });
    User author =
        userRepository
            .findById(authorId)
            .orElseThrow(
                () -> {
                  log.warn("메시지 생성 실패 - 사용자 없음 userId={}", authorId);
                  return new UserNotFoundException(authorId);
                });

    List<BinaryContentCreateRequest> attachmentRequests =
        (binaryContentCreateRequests == null)
            ? Collections.emptyList()
            : binaryContentCreateRequests;

    // attachments 저장 -> DB에는 메타, bytes는 storage에.
    List<BinaryContent> attachments =
        attachmentRequests.stream().map(binaryContentService::create).toList();

    // 이제 생성자 다시 객체로 받음.
    Message message = new Message(messageCreateRequest.content(), channel, author, attachments);
    Message saved = messageRepository.save(message);

    log.debug("메시지 생성 완료 - messageId={}", saved.getId());

    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Message find(UUID messageId) {

    log.debug("메시지 조회 요청 - messageId={}", messageId);

    return messageRepository
        .findById(messageId)
        .orElseThrow(
            () -> {
              log.warn("메시지 조회 실패 - 존재하지 않음 messageId={}", messageId);
              return new MessageNotFoundException(messageId);
            });
  }

  @Transactional(readOnly = true)
  @Override
  public List<Message> findAllByChannel_Id(UUID channelId, Instant cursor, int size) {
    PageRequest pageRequest = PageRequest.of(0, size + 1);
    if (cursor == null) {
      return messageRepository.findByChannel_IdOrderByCreatedAtDesc(channelId, pageRequest);
    }

    return messageRepository.findByChannel_IdAndCreatedAtBeforeOrderByCreatedAtDesc(
        channelId, cursor, pageRequest);
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {

    log.debug("메시지 수정 요청 - messageId={}", messageId);

    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(
                () -> {
                  log.warn("메시지 수정 실패 - 존재하지 않음 messageId={}", messageId);
                  return new MessageNotFoundException(messageId);
                });

    message.update(request.newContent());

    log.debug("메시지 수정 완료 - messageId={}", messageId);

    return message;
  }

  @Override
  public void delete(UUID messageId) {

    log.info("메시지 삭제 요청 - messageId={}", messageId);

    Message message =
        messageRepository
            .findById(messageId)
            .orElseThrow(
                () -> {
                  log.warn("메시지 삭제 실패 - 존재하지 않음 messageId={}", messageId);
                  return new MessageNotFoundException(messageId);
                });

    messageRepository.delete(message);

    log.info("메시지 삭제 완료 - messageId={}", messageId);
  }
}
