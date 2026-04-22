package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.logging.ServiceLogAround;
import com.sprint.mission.discodeit.dto.FileUploadDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.publisher.FileUploadEventPublisher;
import com.sprint.mission.discodeit.exception.channel.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final BasicDomainTemplate domainTemplate;
  private final FileUploadEventPublisher fileUploadEventPublisher;

  @Override
  @ServiceLogAround
  public MessageDto create(MessageCreateRequest request, List<FileUploadDto> attachments) {
    domainTemplate.throwOrNot(request.getChannelId(), channelRepository::existsById,
        id -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, id));
    domainTemplate.throwOrNot(request.getAuthorId(), userRepository::existsById,
        id -> new UserException(UserErrorCode.USERID_NOT_FOUND, id));
    User author = userRepository.getReferenceById(request.getAuthorId());
    Channel channel = channelRepository.getReferenceById(request.getChannelId());
    List<BinaryContent> binaryContents = binaryContentMapper.toEntityFrom(attachments);
    fileUploadEventPublisher.publishAllFileUploadEvent(binaryContents, attachments);
    Message message = Message.builder()
        .content(request.getContent())
        .channel(channel)
        .author(author)
        .attachments(binaryContents)
        .build();
    messageRepository.save(message);
    return messageMapper.toDto(message);
  }

  @Override
  @ServiceLogAround
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findSliceByChannelId(UUID channelId, Pageable pageable) {
    return messageMapper.fromSlice(messageRepository.findSliceByChannelId(channelId, pageable));
  }

  @Override
  @ServiceLogAround
  public MessageDto update(UUID id, MessageUpdateRequest request) {
    Message message = findById(id);
    messageMapper.partialUpdate(request, message);
    return messageMapper.toDto(message);
  }

  @Override
  @ServiceLogAround
  public void delete(UUID id) {
    domainTemplate.deleteByIdOrThrow(id, messageRepository,
        messageId -> new MessageException(MessageErrorCode.MESSAGEID_NOT_FOUND, messageId));
  }

  private Message findById(UUID id) {
    return domainTemplate.getOrThrow(id, messageRepository::findById,
        messageId -> new MessageException(MessageErrorCode.MESSAGEID_NOT_FOUND, messageId));
  }
}
