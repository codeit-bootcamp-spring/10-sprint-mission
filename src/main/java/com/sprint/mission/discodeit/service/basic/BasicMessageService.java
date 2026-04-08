package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final PageResponseMapper pageMapper;
  private final MessageMapper messageMapper;

  @Transactional
  @Override
  public MessageDto createMessage(MessageDto.MessageCreateRequest messageReq,
      List<MultipartFile> attachments) throws IOException {
    log.debug("[Service] 메세지 생성 시작:");
    Channel channel = getChannelOrThrow(messageReq.channelId());
    User author = getUserOrThrow(messageReq.authorId());
    Message msg = new Message(channel, author, messageReq.content());

    if (attachments != null) {
      for (MultipartFile attachment : attachments) {
        BinaryContent content = new BinaryContent(
            attachment.getOriginalFilename(), attachment.getSize(), attachment.getContentType());
        binaryContentRepository.save(content);
        log.debug("[Service] 첨부파일 저장 완료: contentId={}", content.getId());
        binaryContentStorage.put(content.getId(), attachment.getBytes());
        log.debug("[Service] 첨부파일 물리적 저장 완료: contentId={}, contentType={}",
            content.getId(), content.getContentType());
        msg.addAttachment(content);
      }
    }

    messageRepository.save(msg);
    log.debug("[Service] 메세지 저장 완료: id={}", msg.getId());

    log.info("[Service] 메세지 생성 성공: id={}", msg.getId());
    return toResponse(msg);
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Object cursor,
      Pageable pageable) {
    Pageable sizePlusOne = PageRequest.of(0, pageable.getPageSize() + 1, pageable.getSort());

    List<Message> messages = cursor == null
        ? messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, sizePlusOne)
        : messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(channelId,
            (Instant) cursor, sizePlusOne);
    boolean hasNext = messages.size() > pageable.getPageSize();
    Instant nextCursor = null;
    if (hasNext) {
      nextCursor = messages.get(pageable.getPageSize()).getCreatedAt();
      messages = messages.subList(0, pageable.getPageSize());
    }

    List<MessageDto> messagesDto = messages.stream().map(messageMapper::toDto).toList();
    return pageMapper.fromData(messagesDto, nextCursor, hasNext);
  }

  @Transactional
  @Override
  public MessageDto updateMessage(UUID uuid, MessageDto.MessageUpdateRequest messageReq) {
    log.debug("[Service] 메세지 수정 시작: id={}", uuid);
    Message msg = getMessageOrThrow(uuid);

    Optional.ofNullable(messageReq.newContent()).ifPresent(msg::updateMessage);
    messageRepository.save(msg);
    log.debug("[Service] 수정된 메세지 저장 완료: id={}", msg.getId());

    log.info("[Service] 메시지 수정 성공: id={}", msg.getId());
    return toResponse(msg);
  }

  @Transactional
  @Override
  public void deleteMessage(UUID uuid) throws IOException {
    log.debug("[Service] 메세지 삭제 시작: id={}", uuid);
    Message msg = getMessageOrThrow(uuid);

    for (var attachment : msg.getAttachments()) {
      binaryContentStorage.delete(attachment.getId());
      log.debug("[Service] 물리적 이미지 삭제 완료: contentId={}", attachment.getId());
    }

    messageRepository.deleteById(uuid);
    log.info("[Service] 메세지 삭제 성공: id={}", uuid);
  }

  private Message getMessageOrThrow(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException());
  }

  private Channel getChannelOrThrow(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException());
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException());
  }

  private MessageDto toResponse(Message msg) {
    return messageMapper.toDto(msg);
  }
}
