package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelPatchDto;
import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  private final ChannelMapper channelMapper;

  @Override
  public ChannelResponseDto createPublicChannel(PublicChannelPostDto publicChannelPostDto) {
    return channelMapper.fromChannel(
        channelRepository.save(channelMapper.toChannel(publicChannelPostDto)),
        null
    );
  }

  @Override
  public ChannelResponseDto createPrivateChannel(PrivateChannelPostDto privateChannelPostDto) {
    List<User> users = privateChannelPostDto.participantIds().stream()
        .map(userRepository::findById)
        .flatMap(Optional::stream)
        .toList();

    Channel channel = channelRepository.save(
        channelMapper.toChannel(privateChannelPostDto)
    );

    // 유저의 채널 리스트에 채널 id 추가 및 저장
    for (User user : users) {
      user.addChannelId(channel.getId());
      userRepository.save(user);
    }

    return channelMapper.fromChannel(channel, null);
  }

  @Override
  public ChannelResponseDto findById(UUID channelId) {
    Channel channel = channelRepository.findById(channelId).orElseThrow(
        () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
    );

    Instant lastMessageTime = channel.getMessageIds().stream()
        .map(messageRepository::findById)
        .flatMap(Optional::stream)
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);

    return channelMapper.fromChannel(
        channel,
        lastMessageTime
    );
  }

  @Override
  public List<ChannelResponseDto> findAllByUserId(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(
            channel -> channel.getType() == ChannelType.PUBLIC || (
                channel.getType() == ChannelType.PRIVATE &&
                    channel.getUserIds().contains(userId)
            )
        )
        .map(channel -> {
              Instant lastMessageTime = channel.getMessageIds().stream()
                  .map(messageRepository::findById)
                  .flatMap(Optional::stream)
                  .map(Message::getCreatedAt)
                  .max(Instant::compareTo)
                  .orElse(null);

              return channelMapper.fromChannel(
                  channel,
                  lastMessageTime
              );
            }
        ).collect(Collectors.toList());
  }

  @Override
  public ChannelResponseDto update(UUID channelId, ChannelPatchDto channelPatchDto) {
    Channel updateChannel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
        );

    // PRIVATE 채널은 수정할 수 없음
    if (updateChannel.getType() == ChannelType.PRIVATE) {
      throw new BusinessLogicException(ExceptionCode.PRIVATE_CHANNEL_MODIFY_EXCEPTION);
    }

    // 수정
    Optional.ofNullable(channelPatchDto.newName())
        .ifPresent(updateChannel::updateName);
    Optional.ofNullable(channelPatchDto.newDescription())
        .ifPresent(updateChannel::updateDescription);

    channelRepository.save(updateChannel);

    return channelMapper.fromChannel(updateChannel, findLastMessageTime(channelId));
  }

  @Override
  public ChannelResponseDto addUser(UUID channelId, UUID userId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
        );
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );

    channel.addUserId(userId);
    user.addChannelId(channelId);

    channelRepository.save(channel);
    userRepository.save(user);

    return channelMapper.fromChannel(channel, findLastMessageTime(channelId));
  }

  @Override
  public boolean deleteUser(UUID channelId, UUID userId) {
    // 채널 객체를 찾는다.
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
        );
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );

    // 해당 채널에 유저가 속해있는지 확인한 후 내보낸다.
    // 유저쪽도 참여한 채녈 목록에서 삭제한다.
    if (this.isUserInvolved(channelId, userId)) {
      channel.getUserIds().remove(user);
      user.getChannelIds().remove(channel);

      channelRepository.save(channel);
      userRepository.save(user);

      return true;
    }

    return false;
  }

  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
        );

    // message와 readStatus도 삭제한다.
    channel.getMessageIds().forEach(messageRepository::delete);
    readStatusRepository.findByChannelId(channelId).forEach(readStatus -> {
          readStatusRepository.delete(readStatus.getId());
        }
    );

    // 채널 삭제
    channelRepository.delete(channelId);
  }

  @Override
  public boolean isUserInvolved(UUID channelId, UUID userId) {
    // 채널과 유저 객체를 찾는다.
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
        );
    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );

    return channel.getUserIds().contains(user);
  }

  @Override
  public Instant findLastMessageTime(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() ->
            new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
        );

    return channel.getMessageIds().stream()
        .map(messageRepository::findById)
        .flatMap(Optional::stream)
        .map(Message::getCreatedAt)
        .max(Instant::compareTo)
        .orElse(null);
  }
}
