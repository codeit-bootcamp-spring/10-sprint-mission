package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
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
    log.debug("Public 채널 생성 시작: name={}, description={}", request.name(), request.description());
    Channel channel = channelMapper.toEntity(request);
    channelRepository.save(channel);
    log.info("Public 채널 생성 완료: channelId={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto createPrivate(PrivateChannelCreateRequest request) {
    log.debug("Private 채널 생성 시작: participantIdCount={}", request.participantIds().size());
    //채널 생성 후 저장
    Channel channel = channelMapper.toEntity(request);
    channelRepository.save(channel);
    //읽음 상태 생성 후 저장
    request.participantIds().stream()
        .map(userRepository::findById)
        .flatMap(Optional::stream)
        .forEach(user -> readStatusRepository.save(
            new ReadStatus(user, channel, channel.getCreatedAt())));
    log.debug("ReadStatus 생성 완료: readStatusCount={}", request.participantIds().size());
    log.info("Private 채널 생성 완료: channelId={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto findById(UUID channelId) {
    log.debug("채널 조회 시작: channelId={}", channelId);
    //채널Id로 채널 객체 조회
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
    log.debug("채널 조회 완료: channelId={}, channelType={}", channel.getId(), channel.getType());
    return channelMapper.toDto(channel);
  }

  //todo N+1 문제 발생하는 코드
  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.debug("유저가 참여 중인 채널 목록 조회 시작: userId={}", userId);
    List<Channel> channels = channelRepository.findAllByUserId(userId); //쿼리튜닝
    log.debug("유저가 참여 중인 채널 목록 조회 완료: channelCount={}", channels.size());
    return channels.stream()
        .map(channelMapper::toDto) //이 메서드는 매핑 과정에서 MessageRepository와 ReadStatusRepository를 각각 호출
        .toList();
  }

  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("Public 채널 수정 시작: channelId={}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new BusinessLogicException(ExceptionCode.CANNOT_UPDATE_PRIVATE_CHANNEL);
    }
    channel.update(request);
    log.info("Public 채널 수정 완료: channelId={}", channelId);
    return channelMapper.toDto(channel);
  }

  @Override
  public void delete(UUID channelId) {
    log.debug("채널 삭제 시작: channelId={}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
    channelRepository.delete(channel);
    log.info("채널 삭제 완료: channelId={}", channelId);
  }
}
