package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
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
  private final BinaryContentRepository binaryContentRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  @Transactional
  public Message create(String content, UUID authorId, UUID channelId,
      List<MultipartFile> attachments) {
    User author = getOrThrowUser(authorId);
    Channel channel = getOrThrowChannel(channelId);
    validateAccess(authorId, channelId);

    List<BinaryContent> binaryContents = new ArrayList<>();
    if (attachments != null && !attachments.isEmpty()) {
      attachments.stream()
          .filter(file -> !file.isEmpty())
          .forEach(file -> {
            try {
              BinaryContent binaryContent = new BinaryContent(
                  file.getOriginalFilename(),
                  file.getSize(),
                  file.getContentType(),
                  file.getBytes()
              );
              binaryContentRepository.save(binaryContent);
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
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId);
  }

  @Override
  @Transactional
  public Message update(UUID id, String newContent, List<UUID> attachmentIds) {
    Message message = getOrThrowMessage(id);

    // 텍스트 내용 수정
    Optional.ofNullable(newContent).ifPresent(message::updateContent);

    // 첨부파일 수정
    if (attachmentIds != null) {
      List<BinaryContent> newAttachments = binaryContentRepository.findAllById(
          attachmentIds);
      message.updateAttachments(newAttachments);
    }
    return message;
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    Message message = getOrThrowMessage(id);
    messageRepository.delete(message);
  }

  // --- Helper Methods ---

  // 접근 권한 확인 (비공개 채널 여부 체크)
  private void validateAccess(UUID userId, UUID channelId) {
    Channel channel = getOrThrowChannel(channelId);
    if (channel.getType() == ChannelType.PRIVATE) {
      readStatusRepository.findByUserIdAndChannelId(userId, channelId)
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
