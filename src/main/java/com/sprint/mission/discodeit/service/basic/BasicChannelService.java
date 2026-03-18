package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelMapper channelMapper;

  @Override
  public UUID createPrivate(PrivateChannelCreateRequest req) {
    requireNonNull(req, "privateChReq");
    requireNonNull(req.participantIds(), "participantIds");

    if (req.participantIds().isEmpty()) {
      throw new IllegalArgumentException("PRIVATE 채널은 최소 1명 이상의 참여자가 필요합니다.");
    }

    List<User> participants = req.participantIds().stream()
        .map(id -> userRepository.findById(id)
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND)))
        .toList();

    Channel savedChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, null, null));

    Instant now = Instant.now();
    for (User user : participants) {
      ReadStatus rs = new ReadStatus(user, savedChannel);
      rs.updateLastReadAt(now);
      readStatusRepository.save(rs);
    }

    return savedChannel.getId();
  }

  @Override
  public UUID createPublic(PublicChannelCreateRequest req) {
    requireNonNull(req, "publicChReq");
    requireNonNull(req.name(), "name");

    if (req.name().isBlank()) {
      throw new IllegalArgumentException("PUBLIC 채널은 이름이 필요합니다.");
    }

    Channel channel = new Channel(req.name(), req.description());
    return channelRepository.createChannel(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelResponse find(UUID channelId) {
    requireNonNull(channelId, "channelId");

    Channel channel = findChannelOrThrow(channelId);
    Instant lastMessageTime = findLastMessageTime(channel);
    List<UUID> participantIds = channel.isPrivate()
        ? findParticipantIds(channel)
        : List.of();

    return channelMapper.toResponse(channel, lastMessageTime, participantIds);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    requireNonNull(userId, "userId");

    userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    return channelRepository.findAllChannel().stream()
        .filter(channel ->
            !channel.isPrivate()
                || channel.getReadStatuses().stream()
                .anyMatch(readStatus -> readStatus.getUserId().equals(userId))
        )
        .map(channel -> channelMapper.toResponse(
            channel,
            findLastMessageTime(channel),
            channel.isPrivate() ? findParticipantIds(channel) : List.of()
        ))
        .toList();
  }

  @Override
  public ChannelResponse update(ChannelUpdateRequest req) {
    requireNonNull(req, "req");
    requireNonNull(req.channelId(), "channelId");

    Channel channel = findChannelOrThrow(req.channelId());

    if (channel.isPrivate()) {
      throw new BusinessLogicException(ErrorCode.PRIVATE_CHANNEL_CANNOT_BE_UPDATED);
    }

    channel.updateChannel(req.name(), req.description());
    channelRepository.saveChannel(channel);

    return find(req.channelId());
  }

  @Override
  public void delete(UUID channelId) {
    requireNonNull(channelId, "channelId");
    Channel channel = findChannelOrThrow(channelId);
    channelRepository.delete(channel);
  }

  @Override
  @Transactional(readOnly = true)
  public Channel findEntity(UUID channelId) {
    requireNonNull(channelId, "channelId");

    return channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
  }

  private Channel findChannelOrThrow(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
  }

  private Instant findLastMessageTime(Channel channel) {
    return channel.getMessages().stream()
        .map(message -> message.getCreatedAt())
        .max(Instant::compareTo)
        .orElse(null);
  }

  private List<UUID> findParticipantIds(Channel channel) {
    return channel.getReadStatuses().stream()
        .map(ReadStatus::getUserId)
        .toList();
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
    }
  }
}