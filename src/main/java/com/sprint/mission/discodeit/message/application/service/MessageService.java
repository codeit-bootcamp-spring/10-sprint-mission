package com.sprint.mission.discodeit.message.application.service;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import com.sprint.mission.discodeit.message.application.mapper.MessageDomainMapper;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.domain.exception.MessageErrorCode;
import com.sprint.mission.discodeit.message.domain.exception.MessageException;
import com.sprint.mission.discodeit.message.domain.provider.MessageBinaryContentResolver;
import com.sprint.mission.discodeit.message.domain.provider.MessageChannelResolver;
import com.sprint.mission.discodeit.message.domain.provider.MessageUserResolver;
import com.sprint.mission.discodeit.message.domain.repository.MessageRepository;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepository repository;
  private final MessageDomainMapper domainMapper;
  private final MessageChannelResolver channelResolver;
  private final MessageUserResolver userResolver;
  private final MessageBinaryContentResolver binaryContentResolver;
  private final ApplicationEventPublisher eventPublisher;

  @ServiceLogAround
  public Message create(MessageCreateRequest request) {
    User author = userResolver.getOrThrow(request.getAuthorId());
    Channel channel = channelResolver.getProxyOrThrow(request.getChannelId());
    List<BinaryContent> attachments = binaryContentResolver.getOrThrow(request.getAttachmentIds());

    Message message = Message.builder()
        .content(request.getContent())
        .author(author)
        .channel(channel)
        .attachments(attachments)
        .build();
    repository.save(message);
    eventPublisher.publishEvent(domainMapper.toCreatedEvent(message));
    return message;
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public Slice<Message> findSliceByChannelId(UUID channelId, Pageable pageable) {
    Slice<Message> messages = repository.findSliceByChannel_Id(channelId, pageable);
    messages.forEach(message -> Hibernate.initialize(message.getAttachments()));
    return messages;
  }

  @ServiceLogAround
  public Message update(UUID id, MessageUpdateRequest request) {
    Message message = findById(id);
    domainMapper.partialUpdate(request, message);
    eventPublisher.publishEvent(domainMapper.toUpdatedEvent(message));
    return message;
  }

  @ServiceLogAround
  public void delete(UUID id) {
    Message message = findById(id);
    repository.delete(message);
    eventPublisher.publishEvent(domainMapper.toDeletedEvent(message));
  }

  private Message findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findById,
        messageId -> new MessageException(MessageErrorCode.MESSAGEID_NOT_FOUND, messageId));
  }
}
