package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @Override
  public ChannelDto createPublic(PublicChannelCreateRequest request) {
    Channel channel = channelMapper.toEntity(request);
    channelRepository.save(channel);
    log.info("Public 채널 생성 완료: channelId={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
    Channel channel = channelMapper.toEntity(request);
    channelRepository.save(channel);
    request.participantIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new))
        .forEach(user -> readStatusRepository.save(
            new ReadStatus(user, channel, channel.getCreatedAt())));
    log.debug("ReadStatus 생성 완료: readStatusCount={}", request.participantIds().size());
    log.info("Private 채널 생성 완료: channelId={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto findById(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    log.debug("채널 조회 완료: channelId={}, channelType={}", channel.getId(), channel.getType());
    return channelMapper.toDto(channel);
  }

  //todo N+1 문제 발생하는 코드
  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findAllByUserId(userId); //쿼리튜닝
    log.debug("유저가 참여 중인 채널 목록 조회 완료: channelCount={}", channels.size());
    return channels.stream()
        .map(channelMapper::toDto) //이 메서드는 매핑 과정에서 MessageRepository와 ReadStatusRepository를 각각 호출
        .toList();
  }

  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelUpdateException(Map.of("channelId", channelId));
    }
    channel.update(request);
    log.info("Public 채널 수정 완료: channelId={}", channelId);
    return channelMapper.toDto(channel);
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    channelRepository.delete(channel);
    log.info("채널 삭제 완료: channelId={}", channelId);
  }
}
