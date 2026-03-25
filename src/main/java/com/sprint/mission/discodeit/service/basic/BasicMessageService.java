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
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
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
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicMessageService implements MessageService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final PageMapper pageMapper;

  @Override
  public MessageDto create(MessageCreateRequest request, List<MultipartFile> multipartFiles) {

    User user = userRepository.findById(request.authorId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));

    if (channel.getType() == ChannelType.PRIVATE) {
      readStatusRepository.findByUserIdAndChannelId(request.authorId(), request.channelId())
          .orElseThrow(() -> new BusinessLogicException(ExceptionCode.NOT_A_CHANNEL_PARTICIPANT));
    }

    Message message = messageMapper.toEntity(request, channel, user);

    //요청에 첨부파일이 있다면 for-loop를 통해 객체 생성 후 저장
    if (multipartFiles != null && !multipartFiles.isEmpty()) {
      for (MultipartFile file : multipartFiles) {
        if (file.isEmpty()) { //리스트 안에 특정 파일이 비었는지 확인
          continue;
        }
        try {
          BinaryContent attachment = new BinaryContent(
              file.getOriginalFilename(),
              file.getSize(),
              file.getContentType()
          );
          binaryContentRepository.save(attachment);
          binaryContentStorage.put(attachment.getId(), file.getBytes());
          message.addAttachment(attachment); //편의 메서드 사용
        } catch (IOException e) {
          throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_UPLOAD_FAILED);
        }
      }
    }
    messageRepository.save(message); //cascade로 attachment들도 같이 INSERT

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto findById(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {

    Slice<MessageDto> slice = messageRepository.findAllByChannelIdAndCreatedAtLessThan(channelId,
            Optional.ofNullable(cursor).orElse(Instant.now()),
            pageable)
        .map(messageMapper::toDto);

    Instant nextCursor = null;
    if (!slice.getContent().isEmpty() && slice.hasNext()) {
      nextCursor = slice.getContent().get(slice.getContent().size() - 1).createdAt();
    }
    return pageMapper.fromSlice(slice, nextCursor);
  }

  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));

    message.update(request.newContent());
    return messageMapper.toDto(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));
    messageRepository.delete(message);
  }
}
