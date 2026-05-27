package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelLastMessageQueryDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.*;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  @Transactional
  @PreAuthorize("hasRole('CHANNEL_MANAGER')") // 권한 검사
  public Channel createPublicChannel(String name, String description) {
    log.info("Creating new PUBLIC channel with name: {}", name); // 공개 채널 생성 시작 로그

    Channel channel = new Channel(name, description, ChannelType.PUBLIC);
    Channel savedChannel = channelRepository.save(channel);

    log.info("PUBLIC channel created successfully. ID: {}", savedChannel.getId()); // 공개 채널 생성 성공 로그
    return savedChannel;
  }

  @Override
  @Transactional
  public Channel createPrivateChannel(List<UUID> participantIds) {
    log.info("Creating new PRIVATE channel"); // 비공개 채널 생성 시작 로그

    // 유저 검증 및 엔티티 조회
    List<User> participants = participantIds.stream()
        .map(this::getOrThrowUser)
        .toList();

    // 비공개 채널 생성
    Channel channel = new Channel(null, null, ChannelType.PRIVATE);
    channelRepository.save(channel);

    // 참여 유저별 ReadStatu 생성
    participants.forEach(user -> {
      ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
      readStatusRepository.save(readStatus);
    });

    log.info("PRIVATE channel created successfully. ID: {}", channel.getId()); // 비공개 채널 생성 성공 로그
    return channel;
  }

  @Override
  public Channel findById(UUID id) {
    log.debug("Fetching channel by ID: {}", id); // 채널 단건 조회 시작 로그

    Channel channel = getOrThrowChannel(id);

    log.debug("Channel found: {} (Type: {})", channel.getName(),
        channel.getType()); // 채널 단건 조회 성공 로그
    return channel;
  }

  @Override
  public List<Channel> findAllByUserId(UUID userId) {
    log.info("Fetching all channels for user ID: {}", userId); // 특정 유저가 참여한 채널 조회 시작 로그

    // PUBLIC 채널 조회
    List<Channel> publicChannels = channelRepository.findByType(ChannelType.PUBLIC);
    // PRIVATE 채널 조회
    List<Channel> privateChannels = readStatusRepository.findAllByUserIdWithChannel(userId).stream()
        .map(ReadStatus::getChannel)
        .filter(channel -> channel.getType() == ChannelType.PRIVATE)
        .toList();

    List<Channel> allChannels = Stream.concat(publicChannels.stream(), privateChannels.stream())
        .toList();
    log.debug("Total {} channels fetched for user {}", allChannels.size(),
        userId); // 특정 유저가 참여한 채널 조회 성공 로그
    return allChannels;
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('CHANNEL_MANAGER')") // 권한 검사
  public Channel update(UUID id, String newName, String newDescription) {
    log.info("Updating channel with ID: {}", id); // 채널 정보 수정 시작 로그

    Channel channel = getOrThrowChannel(id);

    // PRIVATE 채널은 수정할 수 없음
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelUpdateException(Map.of(
          "channelId", id,
          "channelType", channel.getType(),
          "reason", "PRIVATE 채널은 수정할 수 없습니다."
      ));
    }

    Optional.ofNullable(newName).ifPresent(name -> {
      log.debug("Changing channel name from {} to {}", channel.getName(), name); // 채널 이름 수정 로그
      channel.updateName(name);
    });

    Optional.ofNullable(newDescription).ifPresent(desc -> {
      log.debug("Updating description for channel ID: {}", id); // 채널 설명 수정 로그
      channel.updateDescription(desc);
    });

    log.info("Channel ID {} updated successfully", id); // 채널 정보 수정 성공 로그
    return channel;
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('CHANNEL_MANAGER')") // 권한 검사
  public void deleteById(UUID id) {
    log.info("Deleting channel with ID: {}", id); // 채널 삭제 시작 로그

    Channel channel = getOrThrowChannel(id);
    channelRepository.delete(channel);

    log.info("Channel ID {} deleted successfully", id); // 채널 삭제 성공 로그
  }

  // --- Helper Methods ---

  // 채널 DTO 변환 시, 마지막 메시지 작성 시간 필드(lastMessageAt)를 위한 메서드 - 단건 조회용
  @Override
  public Instant getLastMessageAt(UUID channelId) {
    log.debug("Fetching last message time for channel: {}", channelId); // 마지막 메시지 작성 시간 조회 시작 로그
    return messageRepository.findLastMessageAtByChannelId(channelId).orElse(null);
  }

  // 채널 DTO 변환 시, 참여자 정보 필드(participants)를 위한 메서드 - 단건 조회용
  @Override
  public List<User> getParticipants(UUID channelId) {
    log.debug("Fetching participants for channel: {}", channelId); // 참여자 정보 조회 시작 로그
    return readStatusRepository.findAllByChannelIdWithUser(channelId).stream()
        .map(ReadStatus::getUser)
        .toList();
  }

  // 채널 DTO 변환 시, 마지막 메시지 작성 시간 필드(lastMessageAt)를 위한 메서드 - 일괄 조회용
  @Override
  public Map<UUID, Instant> getLastMessagesAtMap(List<UUID> channelIds) {
    log.debug("Fetching last messages map for {} channels",
        channelIds.size()); // 마지막 메시지 작성 시간 일괄 조회 시작 로그
    List<ChannelLastMessageQueryDto> results = messageRepository.findLastMessagesByChannelIds(
        channelIds);

    log.debug("Successfully created last messages map (size: {})", results.size()); // 일괄 조회 확인 로그
    // 리스트를 {채널ID:시간} 형태의 Map으로 변환
    return results.stream()
        .collect(Collectors.toMap(
            ChannelLastMessageQueryDto::channelId,
            ChannelLastMessageQueryDto::lastMessageAt
        ));
  }

  // 채널 DTO 변환 시, 참여자 정보 필드(participants)를 위한 메서드 - 일괄 조회용
  @Override
  public Map<UUID, List<User>> getParticipantsMap(List<UUID> channelIds) {
    log.debug("Fetching participants map for {} channels", channelIds.size()); // 참여자 정보 일괄 조회 시작 로그
    List<ReadStatus> allReadStatuses = readStatusRepository.findAllByChannelIdsWithUser(channelIds);

    log.debug("Successfully created participants map from {} read statuses",
        allReadStatuses.size()); // 일괄 조회 확인 로그
    // 리스트를 {채널ID:유저리스트} 형태의 Map으로 변환
    return allReadStatuses.stream()
        .collect(Collectors.groupingBy(
            rs -> rs.getChannel().getId(),
            Collectors.mapping(ReadStatus::getUser, Collectors.toList())
        ));
  }

  // 채널 검증
  private Channel getOrThrowChannel(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("requestedChannelId", id)));
  }

  // 유저 검증
  private User getOrThrowUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(Map.of("requestedUserId", id)));
  }
}
