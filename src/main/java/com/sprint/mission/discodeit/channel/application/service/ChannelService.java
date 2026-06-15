package com.sprint.mission.discodeit.channel.application.service;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.event.ReadStatusCreatedEvent;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelErrorCode;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelException;
import com.sprint.mission.discodeit.channel.domain.provider.ChannelUserResolver;
import com.sprint.mission.discodeit.channel.infra.repository.ChannelRepository;
import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.channel.presentation.mapper.ChannelMapper;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelService {

  private final ChannelRepository repository;
  private final ChannelMapper mapper;
  private final ApplicationEventPublisher eventPublisher;
  private final ChannelUserResolver userProvider;

  @ServiceLogAround
  public ChannelResponse createPublic(PublicChannelCreateRequest request) {
    Channel channel = mapper.toEntityFrom(request);
    repository.save(channel);
    return mapper.toDto(channel);
  }

  @ServiceLogAround
  public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
    Channel channel = mapper.toEntityFrom(request);
    repository.save(channel);
    List<User> participants = userProvider.getOrThrow(request.getParticipantIds());
    eventPublisher.publishEvent(
        new ReadStatusCreatedEvent(channel.getId(), request.getParticipantIds(), true));
    return mapper.toDtoFrom(channel, participants);
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public ChannelResponse find(UUID id) {
    ChannelDetailDto channelDetail = findById(id);
    return mapper.toDto(channelDetail);
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    List<ChannelDetailDto> channelDetails = repository.findVisibleChannelDetails(
        userId);
    return mapper.toDto(channelDetails);
  }

  @ServiceLogAround
  public ChannelResponse update(UUID id, PublicChannelUpdateRequest request) {
    ChannelDetailDto channelDetail = findById(id);
    mapper.partialUpdate(request, channelDetail.channel());
    return mapper.toDto(channelDetail);
  }

  @ServiceLogAround
  public void delete(UUID id) {
    DomainServiceSupport.deleteOrThrow(id, repository,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
  }

  private ChannelDetailDto findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findChannelDetailById,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
  }

}
