package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserRepository userRepository;

  @Override
  public ChannelResponse createPublic(PublicChannelCreateRequest createRequest) {
    Channel channel = new Channel(
        ChannelType.PUBLIC,
        createRequest.name(),
        createRequest.description()
    );
    channelRepository.save(channel);

    return ChannelResponse.of(channel, List.of(), null);
  }

  @Override
  public ChannelResponse createPrivate(PrivateChannelCreateRequest createRequest) {
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

    return ChannelResponse.of(channel, createRequest.participantIds(), null);
  }

  @Override
  public ChannelResponse findById(UUID channelId) {
    //채널Id로 채널 객체 조회
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
    return ChannelResponse.of(
        channel,
        getParticipantIds(channelId),
        getLastMessageAt(channelId));
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    List<Channel> channelList = channelRepository.findAccessibleChannelsByUserId(
        userId); //쿼리튜닝

    return channelList.stream()
        .map(channel -> ChannelResponse.of(
            channel,
            getParticipantIds(channel.getId()),
            getLastMessageAt(channel.getId())
        ))
        .toList();
  }

  @Override
  public ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new BusinessLogicException(ExceptionCode.CANNOT_UPDATE_PRIVATE_CHANNEL);
    }
    Optional.ofNullable(request.newName()).ifPresent(channel::updateChannelName);
    Optional.ofNullable(request.newDescription()).ifPresent(channel::updateDescription);

    channelRepository.save(channel);
    return findById(channel.getId());
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));
    //todo 채널 내부에 있는 메시지 첨부파일들 삭제하는 로직 추가 필요
    channelRepository.delete(channel);
  }

  private Instant getLastMessageAt(UUID channelId) {
    return messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channelId)
        .map(Message::getCreatedAt)
        .orElse(null);
  }

  private List<UUID> getParticipantIds(UUID channelId) {
    return readStatusRepository.findAllByChannelId(channelId).stream()
        .map(ReadStatus::getUser)
        .map(User::getId)
        .toList();
  }
}
