package com.sprint.mission.discodeit.service.basic;

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
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService extends BasicDomainService<Channel> implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;
  private final ReadStatusMapper readStatusMapper;

  @Override
  public ChannelDto createPublic(PublicChannelCreateRequest request) {
    Channel channel = channelMapper.toEntityFrom(request);
    channelRepository.save(channel);
    return channelMapper.toDto(channel);
  }

  // todo: [warn] user id not found
  @Override
  public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
    Channel channel = channelMapper.toEntityFrom(request);
    channelRepository.save(channel);
    List<User> participants = userRepository.filterExistingIds(request.getParticipantIds())
        .stream()
        .map(userRepository::getReferenceById)
        .toList();
    List<ReadStatus> readStatuses = readStatusMapper.toEntityFrom(channel, participants);
    readStatusRepository.saveAll(readStatuses);
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID id) {
    Channel channel = findById(id);
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findVisibleToWithLastMessageAt(userId);
    return channelMapper.toDto(channels);
  }

  @Override
  public ChannelDto update(UUID id, PublicChannelUpdateRequest request) {
    ensure(request.getType(), Predicate.not(ChannelType.PUBLIC::equals),
        value -> new ChannelException(ChannelErrorCode.PRIVATE_CHANNEL_CANT_BE_UPDATED, id));

    Channel channel = findById(id);
    channelMapper.partialUpdate(request, channel);
    return channelMapper.toDto(channel);
  }

  @Override
  public void delete(UUID id) {
    deleteByIdOrThrow(id, channelRepository,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
  }

  @Override
  protected Channel findById(UUID id) {
    return getOrThrow(id, channelRepository::findById,
        value -> new ChannelException(ChannelErrorCode.CHANNELID_NOT_FOUND, value));
  }

}
