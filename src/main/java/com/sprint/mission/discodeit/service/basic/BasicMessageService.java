package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public Message create(String content, UUID authorId, UUID channelId,
      List<MultipartFile> attachments) {
    User author = getOrThrowUser(authorId);
    Channel channel = getOrThrowChannel(channelId);
    validateAccess(authorId, channel);

    List<BinaryContent> binaryContents = new ArrayList<>();
    if (attachments != null && !attachments.isEmpty()) {
      attachments.stream()
          .filter(file -> !file.isEmpty())
          .forEach(file -> {
            try {
              BinaryContent binaryContent = new BinaryContent(
                  file.getOriginalFilename(),
                  file.getSize(),
                  file.getContentType()
              );
              binaryContentRepository.save(binaryContent);

              binaryContentStorage.put(binaryContent.getId(), file.getBytes());

              binaryContents.add(binaryContent);
            } catch (IOException e) {
              throw new RuntimeException("파일 저장 오류", e);
            }
          });
    }

    Message newMessage = new Message(content, author, channel, binaryContents);
    return messageRepository.save(newMessage);
  }

  @Override
  public Message findById(UUID id) {
    return getOrThrowMessage(id);
  }

  @Override
  public Slice<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable) {

    // 커서가 없을 시, 현재가 기준 (최신 메시지부터 조회)
    Instant targetTime = Optional.ofNullable(cursor).orElse(Instant.now());
    return messageRepository.findAllByChannelIdWithAuthor(channelId, targetTime, pageable);
  }

  @Override
  @Transactional
  public Message update(UUID id, String newContent) {
    Message message = getOrThrowMessage(id);

    // 텍스트 내용만 수정
    Optional.ofNullable(newContent).ifPresent(message::updateContent);
    return message;
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    Message message = getOrThrowMessage(id);
    messageRepository.delete(message);
  }

  // --- Helper Methods ---

  // 비공개 채널 접근 권한 확인
  private void validateAccess(UUID userId, Channel channel) {
    if (channel.getType() == ChannelType.PRIVATE) {
      readStatusRepository.findByUserIdAndChannelId(userId, channel.getId())
          .orElseThrow(() -> new IllegalArgumentException("채널 접근 권한이 없습니다."));
    }
  }

  // 유저 검증
  private User getOrThrowUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
  }

  // 채널 검증
  private Channel getOrThrowChannel(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
  }

  // 메시지 검증
  private com.sprint.mission.discodeit.entity.Message getOrThrowMessage(UUID id) {
    return messageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 메시지를 찾을 수 없습니다."));
  }
}
