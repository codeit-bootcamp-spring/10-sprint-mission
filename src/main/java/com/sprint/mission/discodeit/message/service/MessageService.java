package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.api.response.PageResponse;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.response.MessageResponse;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.domain.exception.MessageErrorCode;
import com.sprint.mission.discodeit.message.domain.exception.MessageException;
import com.sprint.mission.discodeit.message.presentation.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.domain.provider.MessageBinaryContentResolver;
import com.sprint.mission.discodeit.message.domain.provider.MessageChannelResolver;
import com.sprint.mission.discodeit.message.domain.provider.MessageUserResolver;
import com.sprint.mission.discodeit.message.infra.repository.MessageRepository;
import com.sprint.mission.discodeit.user.domain.entity.User;
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

  private final MessageRepository repository;
  private final MessageMapper mapper;
  private final MessageChannelResolver channelProvider;
  private final MessageUserResolver userProvider;
  private final MessageBinaryContentResolver binaryContentProvider;

  @ServiceLogAround
  public MessageResponse create(MessageCreateRequest request) {
    User author = userProvider.getProxyOrThrow(request.getAuthorId());
    Channel channel = channelProvider.getProxyOrThrow(request.getChannelId());
    List<BinaryContent> attachments = binaryContentProvider.getProxyOrThrow(request.getAttachmentIds());

    Message message = Message.builder()
        .content(request.getContent())
        .channel(channel)
        .author(author)
        .attachments(attachments)
        .build();
    repository.save(message);
    return mapper.toDto(message);
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public PageResponse<MessageResponse> findSliceByChannelId(UUID channelId, Pageable pageable) {
    return mapper.fromSlice(repository.findSliceByChannel_Id(channelId, pageable));
  }

  @ServiceLogAround
  public MessageResponse update(UUID id, MessageUpdateRequest request) {
    Message message = findById(id);
    mapper.partialUpdate(request, message);
    return mapper.toDto(message);
  }

  @ServiceLogAround
  public void delete(UUID id) {
    DomainServiceSupport.deleteOrThrow(id, repository,
        messageId -> new MessageException(MessageErrorCode.MESSAGEID_NOT_FOUND, messageId));
  }

  private Message findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findById,
        messageId -> new MessageException(MessageErrorCode.MESSAGEID_NOT_FOUND, messageId));
  }
}
