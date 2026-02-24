package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public UUID createPrivate(PrivateChannelCreateRequest req) {
    requireNonNull(req, "privateChReq");
    requireNonNull(req.participantIds(), "participantIds");
    if (req.participantIds().isEmpty()) {
      throw new IllegalArgumentException("PRIVATE 채널은 최소 1명 이상의 참여자가 필요합니다.");
    }

    List<User> participants = req.participantIds().stream()
        .map(id -> {
          User u = userRepository.findById(id);
          if (u == null) {
            throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
          }
          return u;
        })
        .toList();

    Channel channel = new Channel(participants);
    UUID channelId = channelRepository.createChannel(channel);

    participants.forEach(user -> {
      ReadStatus rs = new ReadStatus(user.getId(), channelId);
      rs.updateLastReadAt(Instant.now());
      readStatusRepository.save(rs);
    });

    return channelId;
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
  public ChannelResponse find(UUID channelId) {
    requireNonNull(channelId, "channelId");

    Channel channel = findChannelOrThrow(channelId);

    Instant lastMessageTime = messageRepository.findAllByChannelId(channelId).stream()
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);

    List<UUID> participantIds = channel.isPrivate()
        ? channel.getParticipants().stream().map(User::getId).toList()
        : List.of();

    return new ChannelResponse(
        channel.getId(),
        channel.getChannelName(),
        channel.getDescription(),
        channel.isPrivate(),
        lastMessageTime,
        participantIds
    );
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    requireNonNull(userId, "userId");

    if (userRepository.findById(userId) == null) {
      throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
    }

    return channelRepository.findAllChannel().stream()
        .filter(channel ->
            !channel.isPrivate()
                || channel.getParticipants().stream().anyMatch(u -> u.getId().equals(userId))
        )
        .map(channel -> {
          Instant lastMessageTime = messageRepository.findAllByChannelId(channel.getId()).stream()
              .map(Message::getCreatedAt)
              .max(Instant::compareTo)
              .orElse(null);

          List<UUID> participantIds = channel.isPrivate()
              ? channel.getParticipants().stream().map(User::getId).toList()
              : List.of();

          return new ChannelResponse(
              channel.getId(),
              channel.getChannelName(),
              channel.getDescription(),
              channel.isPrivate(),
              lastMessageTime,
              participantIds
          );
        })
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

    findChannelOrThrow(channelId);

    messageRepository.findAllByChannelId(channelId)
        .forEach(m -> messageRepository.delete(m.getId()));

    readStatusRepository.deleteByChannelId(channelId);
    channelRepository.deleteChannel(channelId);
  }

  @Override
  public Channel findEntity(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
  }

  private Channel findChannelOrThrow(UUID channelId) {
    Channel channel = channelRepository.findChannel(channelId);
    if (channel == null) {
      throw new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND);
    }
    return channel;
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
    }
  }
}
