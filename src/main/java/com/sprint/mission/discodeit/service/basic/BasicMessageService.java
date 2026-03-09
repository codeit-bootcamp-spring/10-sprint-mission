package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
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
  public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {
    User author = getOrThrowUser(request.authorId());
    Channel channel = getOrThrowChannel(request.channelId());
    validateAccess(request.authorId(), request.channelId());

    List<BinaryContent> binaryContents = new ArrayList<>();
    if (attachments != null && !attachments.isEmpty()) {
      attachments.stream()
          .filter(file -> !file.isEmpty())
          .forEach(file -> {
            try {
              BinaryContent content = new BinaryContent(
                  file.getOriginalFilename(),
                  file.getSize(),
                  file.getContentType(),
                  file.getBytes()
              );
              binaryContentRepository.save(content);
              binaryContents.add(content);
            } catch (IOException e) {
              throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
            }
          });
    }

    Message newMessage = new Message(request.content(), author, channel, binaryContents);

    messageRepository.save(newMessage);
    return toDto(newMessage);
  }


  @Override
  public MessageDto findById(UUID id) {
    Message message = getOrThrowMessage(id);
    return toDto(message);
  }

  @Override
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  @Transactional
  public MessageDto update(UUID id, MessageUpdateRequest request) {
    Message message = getOrThrowMessage(id);

    // 텍스트 내용 수정
    Optional.ofNullable(request.newContent()).ifPresent(message::updateContent);

    // 첨부파일 수정
    if (request.attachmentIds() != null) {
      List<BinaryContent> newAttachments = binaryContentRepository.findAllById(
          request.attachmentIds());
      message.updateAttachments(newAttachments);
    }
    return toDto(message);
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

  // 첨부파일 검증
  private void validateBinaryContentExists(UUID id) {
    if (!binaryContentRepository.findById(id).isPresent()) {
      throw new NoSuchElementException("해당 첨부파일을 찾을 수 없습니다.");
    }
  }

  // 엔티티 -> DTO 변환
  private MessageDto toDto(Message message) {
    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        message.getAuthor() != null ? message.getAuthor().getId() : null,
        message.getAttachments().stream().map(BinaryContent::getId).toList()

    );
  }
}
