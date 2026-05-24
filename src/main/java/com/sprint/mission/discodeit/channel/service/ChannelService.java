package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.channel.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.exception.ChannelErrorCode;
import com.sprint.mission.discodeit.channel.exception.ChannelException;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.channel.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;
  private final ReadStatusMapper readStatusMapper;
  private final DomainServiceSupport domainTemplate;

  @ServiceLogAround
  public ChannelResponse createPublic(PublicChannelCreateRequest request) {
    Channel channel = channelMapper.toEntityFrom(request);
    channelRepository.save(channel);
    return channelMapper.toDto(channel);
  }

  // todo: [warn] user id not found
  @ServiceLogAround
  public ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
    Channel channel = channelMapper.toEntityFrom(request);
    channelRepository.save(channel);
    List<User> participants = userRepository.findProfileAndStatusByIdIn(
        request.getParticipantIds());
    List<ReadStatus> readStatuses = readStatusMapper.toEntityFrom(channel, participants);
    readStatusRepository.saveAll(readStatuses);
    return channelMapper.toDtoFrom(channel, participants);
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public ChannelResponse find(UUID id) {
    ChannelDetailDto channelDetail = findById(id);
    return channelMapper.toDto(channelDetail);
  }

  @ServiceLogAround
  @Transactional(readOnly = true)
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    List<ChannelDetailDto> channelDetails = channelRepository.findVisibleChannelDetails(
        userId);
    return channelMapper.toDto(channelDetails);
  }

  @ServiceLogAround
  public ChannelResponse update(UUID id, PublicChannelUpdateRequest request) {
    ChannelDetailDto channelDetail = findById(id);
    channelMapper.partialUpdate(request, channelDetail.channel());
    return channelMapper.toDto(channelDetail);
  }

  @ServiceLogAround
  public void delete(UUID id) {
    domainTemplate.deleteByIdOrThrow(id, channelRepository,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
  }

  private ChannelDetailDto findById(UUID id) {
    return domainTemplate.getOrThrow(id, channelRepository::findChannelDetailById,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
  }

}
