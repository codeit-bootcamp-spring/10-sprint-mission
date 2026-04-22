package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.logging.ServiceLogAround;
import com.sprint.mission.discodeit.dto.ChannelDetailResponse;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;
  private final ReadStatusMapper readStatusMapper;
  private final BasicDomainTemplate domainTemplate;

  @Override
  @ServiceLogAround
  public ChannelDto createPublic(PublicChannelCreateRequest request) {
    Channel channel = channelMapper.toEntityFrom(request);
    channelRepository.save(channel);
    return channelMapper.toDto(channel);
  }

  // todo: [warn] user id not found
  @Override
  @ServiceLogAround
  public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
    Channel channel = channelMapper.toEntityFrom(request);
    channelRepository.save(channel);
    List<User> participants = userRepository.findProfileAndStatusByIdIn(
        request.getParticipantIds());
    List<ReadStatus> readStatuses = readStatusMapper.toEntityFrom(channel, participants);
    readStatusRepository.saveAll(readStatuses);
    return channelMapper.toDtoFrom(channel, participants);
  }

  @Override
  @ServiceLogAround
  @Transactional(readOnly = true)
  public ChannelDto find(UUID id) {
    ChannelDetailResponse channelDetail = findById(id);
    return channelMapper.toDto(channelDetail);
  }

  @Override
  @ServiceLogAround
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<ChannelDetailResponse> channelDetails = channelRepository.findVisibleChannelDetails(
        userId);
    return channelMapper.toDto(channelDetails);
  }

  @Override
  @ServiceLogAround
  public ChannelDto update(UUID id, PublicChannelUpdateRequest request) {
    ChannelDetailResponse channelDetail = findById(id);
    channelMapper.partialUpdate(request, channelDetail.channel());
    return channelMapper.toDto(channelDetail);
  }

  @Override
  @ServiceLogAround
  public void delete(UUID id) {
    domainTemplate.deleteByIdOrThrow(id, channelRepository,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
  }

  private ChannelDetailResponse findById(UUID id) {
    return domainTemplate.getOrThrow(id, channelRepository::findChannelDetailById,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
  }

}
