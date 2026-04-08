package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.common.CommonErrorCode;
import com.sprint.mission.discodeit.exception.common.CommonException;
import com.sprint.mission.discodeit.exception.message.MessageErrorCode;
import com.sprint.mission.discodeit.exception.message.MessageException;
import com.sprint.mission.discodeit.exception.user.UserErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService extends BasicDomainService<Message> implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public MessageDto create(MessageCreateRequest request, List<MultipartFile> attachments) {
    ensure(request.getChannelId(), channelRepository::existsById,
        id -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, id));
    ensure(request.getAuthorId(), userRepository::existsById,
        id -> new UserException(UserErrorCode.USERID_NOT_FOUND, id));
    User author = userRepository.getReferenceById(request.getAuthorId());
    Channel channel = channelRepository.getReferenceById(request.getChannelId());
    try {
      List<BinaryContent> files = binaryContentMapper.toEntityFrom(attachments);
      Message message = Message.builder()
          .content(request.getContent())
          .channel(channel)
          .author(author)
          .attachments(files)
          .build();
      messageRepository.save(message);
      return messageMapper.toDto(message);
    } catch (IOException e) {
      throw new CommonException(CommonErrorCode.FILE_CANT_READ);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    return messageMapper.toDto(messageRepository.findAllByChannelId(channelId));
  }

  @Override
  public MessageDto update(UUID id, MessageUpdateRequest request) {
    Message message = findById(id);
    messageMapper.partialUpdate(request, message);
    return messageMapper.toDto(message);
  }

  @Override
  public void delete(UUID id) {
    deleteByIdOrThrow(id, messageRepository,
        messageId -> new MessageException(MessageErrorCode.MESSAGEID_NOT_FOUND, messageId));
  }

  @Override
  protected Message findById(UUID id) {
    return getOrThrow(id, messageRepository::findById,
        messageId -> new MessageException(MessageErrorCode.MESSAGEID_NOT_FOUND, messageId));
  }

}
