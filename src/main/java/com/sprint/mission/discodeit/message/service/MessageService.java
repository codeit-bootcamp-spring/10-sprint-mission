package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.exception.ChannelErrorCode;
import com.sprint.mission.discodeit.channel.exception.ChannelException;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.common.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.common.dto.response.PageResponse;
import com.sprint.mission.discodeit.common.storage.event.FileUploadEventPublisher;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import com.sprint.mission.discodeit.message.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.dto.response.MessageResponse;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.exception.MessageErrorCode;
import com.sprint.mission.discodeit.message.exception.MessageException;
import com.sprint.mission.discodeit.message.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.exception.UserException;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final DomainServiceSupport domainTemplate;
  private final FileUploadEventPublisher fileUploadEventPublisher;

  @ServiceLogAround
  public MessageResponse create(MessageCreateRequest request, List<FileUploadRequest> attachments) {
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

  @ServiceLogAround
  @Transactional(readOnly = true)
  public PageResponse<MessageResponse> findSliceByChannelId(UUID channelId, Pageable pageable) {
    return messageMapper.fromSlice(messageRepository.findSliceByChannelId(channelId, pageable));
  }

  @ServiceLogAround
  public MessageResponse update(UUID id, MessageUpdateRequest request) {
    Message message = findById(id);
    messageMapper.partialUpdate(request, message);
    return messageMapper.toDto(message);
  }

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
