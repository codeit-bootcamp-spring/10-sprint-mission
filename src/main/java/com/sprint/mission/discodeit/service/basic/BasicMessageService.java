package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessagePatchDto;
import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.AttachmentUtil;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;

  private final MessageMapper messageMapper;
  private final AttachmentUtil attachmentUtil;

  @Override
  public MessageResponseDto create(MessagePostDto messagePostDto, List<MultipartFile> attachments)
      throws RuntimeException {
    // 메시지를 생성 전, 유저가 해당 채널에 속해있는지 확인한다.
    Channel channel = channelRepository.findById(messagePostDto.channelId())
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND,
                messagePostDto.channelId())
        );
    User user = userRepository.findById(messagePostDto.authorId())
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND,
                messagePostDto.authorId())
        );

    // private channel은 참가자만 메시지를 보낼 수 있음.
    if (channel.getType() == ChannelType.PRIVATE && user.getChannelIds().stream()
        .noneMatch(chId -> chId.equals(messagePostDto.channelId()))) {
      throw new BusinessLogicException(ExceptionCode.NOT_INVITED_IN_CHANNEL);
    }

    Message newMessage = messageRepository.save(messageMapper.toMessage(messagePostDto));

    // channel과 user의 messageList에 현재 message를 add
    channel.addMessage(newMessage.getId());
    channelRepository.save(channel);

    user.addMessageId(newMessage.getId());
    userRepository.save(user);

    // 선택적으로 첨부파일 등록(첨부파일 영속화 + 레코드 추가 + 양방향 연결)
    Optional.ofNullable(attachments).ifPresent(attachmentList -> {
      attachmentList.forEach(attachment -> {
        try {
          UUID randomId = UUID.randomUUID();
          BinaryContent binaryContent = new BinaryContent(
              null,
              newMessage.getId(),
              randomId + "_" + attachment.getOriginalFilename(),
              (int) attachment.getSize(),
              attachment.getContentType(),
              attachment.getBytes()
          );
          attachmentUtil.saveOne(randomId, attachment);
          binaryContentRepository.save(binaryContent);

          newMessage.addAttachmentId(binaryContent.getId()); // 메시지에 파일 id 정보 업데이트
          messageRepository.save(newMessage);
        } catch (IOException e) {
          e.printStackTrace();
          throw new BusinessLogicException(ExceptionCode.ATTACHMENT_SAVE_EXCEPTION);
        }
      });

    });

    return messageMapper.toResponse(newMessage);
  }

  @Override
  public MessageResponseDto findById(UUID id) {
    return messageMapper.toResponse(messageRepository.findById(id)
        .orElseThrow(() ->
            new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND, id)
        )
    );
  }

  @Override
  public List<MessageResponseDto> findByUser(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userId)
        );

    return user.getMessageIds().stream()
        .map(messageRepository::findById)
        .flatMap(Optional::stream)
        .map(messageMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<MessageResponseDto> findByChannelId(UUID channelId) {
    return channelRepository.findById(channelId)
        .stream()
        .map(Channel::getMessageIds)
        .flatMap(Collection::stream)
        .map(messageRepository::findById)
        .flatMap(Optional::stream)
        .map(messageMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  public MessageResponseDto updateById(UUID messageId, MessagePatchDto messagePatchDto) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND, messageId)
        );

    message.updateContent(messagePatchDto.newContent());
    messageRepository.save(message);

    return messageMapper.toResponse(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND, messageId)
        );

    // 유저쪽에서 메시지 정보 삭제
    User user = userRepository.findById(message.getAuthorId())
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, message.getAuthorId())
        );
    user.getMessageIds()
        .removeIf(msgId -> msgId.equals(messageId));
    userRepository.save(user);

    // 채널쪽에 메시지 정보 삭제
    Channel channel = channelRepository.findById(message.getChannelId())
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND,
                message.getChannelId())
        );
    channel.getMessageIds()
        .removeIf(msgId -> msgId.equals(messageId));
    channelRepository.save(channel);

    // binaryContent도 삭제
    message.getAttachmentIds().forEach(binaryContentRepository::delete);

    // 실제 메시지 객체 삭제
    messageRepository.delete(messageId);
  }
}
