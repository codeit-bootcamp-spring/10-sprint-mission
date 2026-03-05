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
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @Override
  public ChannelDto createPublic(PublicChannelCreateRequest createRequest) {
    Channel channel = new Channel(
        ChannelType.PUBLIC,
        createRequest.name(),
        createRequest.description()
    );
    channelRepository.save(channel);

    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto createPrivate(PrivateChannelCreateRequest createRequest) {
    //채널 생성 후 저장
    Channel channel = new Channel(
        ChannelType.PRIVATE,
        null,
        null
    );
    channelRepository.save(channel);

    //읽음 상태 생성 후 저장
    createRequest.participantIds().stream()
        .map(userRepository::findById)
        .flatMap(Optional::stream)
        .forEach(user -> readStatusRepository.save(new ReadStatus(user, channel)));

    return channelMapper.toDto(channel);
  }

  @Override
  public ChannelDto findById(UUID channelId) {
    //채널Id로 채널 객체 조회
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
    return channelMapper.toDto(channel);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> channelList = channelRepository.findAccessibleChannelsByUserId(
        userId); //쿼리튜닝

    return channelList.stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new BusinessLogicException(ExceptionCode.CANNOT_UPDATE_PRIVATE_CHANNEL);
    }
    Optional.ofNullable(request.newName()).ifPresent(channel::updateChannelName);
    Optional.ofNullable(request.newDescription()).ifPresent(channel::updateDescription);

    channelRepository.save(channel);
    return channelMapper.toDto(channel);
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
    //채널의 연관 데이터 (메시지, ReadStatus) 처리 여부 추가 필요
    channelRepository.delete(channel);
  }

}
