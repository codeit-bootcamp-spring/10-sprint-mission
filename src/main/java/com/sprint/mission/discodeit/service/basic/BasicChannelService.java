package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  // PUBLIC 채널 생성
  @Override
  public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
    Channel channel = new Channel(
        request.name(),
        request.description(),
        ChannelType.PUBLIC
    );
    channelRepository.save(channel);
    return toDto(channel, null, null);
  }

  // PRIVATE 채널 생성
  @Override
  public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(null, null, ChannelType.PRIVATE);
    channelRepository.save(channel);

    // 참여 유저별 ReadStatus 정보 생성
    request.participantIds().forEach(memberId -> {
      validateUserExists(memberId);

      ReadStatus readStatus = new ReadStatus(memberId, channel.getId(), Instant.now());
      readStatusRepository.save(readStatus);
    });

    return toDto(channel, null, request.participantIds());
  }

  // 단건 조회
  @Override
  public ChannelDto findById(UUID id) {
    Channel channel = getOrThrowChannel(id);
    return toDto(channel, getLastMessageAt(id), getMemberIdsIfPrivate(channel));
  }

  // 유저별 참여하고 있는 채널 전체 조회
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // PUBLIC 채널 조회
    Stream<Channel> publicStream = channelRepository.findAllPublic().stream();
    // PRIVATE 채널 조회
    Stream<Channel> privateStream = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatus -> channelRepository.findById(readStatus.getChannelId()).orElse(null))
        .filter(Objects::nonNull)
        .filter(channel -> channel.getType() == ChannelType.PRIVATE);

    return Stream.concat(publicStream, privateStream)
        .map(channel -> {
          Instant lastMessageAt = getLastMessageAt(channel.getId());
          List<UUID> memberIds = getMemberIdsIfPrivate(channel);
          return toDto(channel, lastMessageAt, memberIds);
        })
        .toList();
  }

  // 채널 정보 수정
  @Override
  public ChannelDto update(UUID id, PublicChannelUpdateRequest request) {
    Channel channel = getOrThrowChannel(id);

    // PRIVATE 채널은 수정할 수 없음
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new IllegalStateException("PRIVATE 채널은 수정할 수 없습니다.");
    }

    Optional.ofNullable(request.newName()).ifPresent(channel::updateName);
    Optional.ofNullable(request.newDescription()).ifPresent(channel::updateDescription);

    channelRepository.save(channel);
    return toDto(channel, getLastMessageAt(id), getMemberIdsIfPrivate(channel));
  }

  // 채널 삭제
  @Override
  public void deleteById(UUID id) {
    getOrThrowChannel(id);

    messageRepository.deleteByChannelId(id);
    readStatusRepository.deleteByChannelId(id);
    channelRepository.deleteById(id);
  }


  // 특정 채널의 가장 최근 메시지 시각 조회
  private Instant getLastMessageAt(UUID channelId) {
    return messageRepository.findAll().stream()
        .filter(m -> m.getChannel().getId().equals(channelId))
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder()) // 가장 큰 값(최근 시간) 찾기
        .orElse(null);
  }

  // 비공개 채널인 경우 참여자 ID 목록 조회
  private List<UUID> getMemberIdsIfPrivate(Channel channel) {
    if (channel.getType() != ChannelType.PRIVATE) {
      return null;
    }
    return readStatusRepository.findAllByChannelId(channel.getId()).stream()
        .map(ReadStatus::getUserId)
        .toList();
  }

  // 채널 검증
  private Channel getOrThrowChannel(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
  }

  // 유저 검증
  private void validateUserExists(UUID userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
  }

  // 엔티티 -> DTO 변환
  private ChannelDto toDto(Channel channel, Instant lastMessageAt,
      List<UUID> participantIds) {
    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participantIds,
        lastMessageAt,
        channel.getCreatedAt(),
        channel.getUpdatedAt()
    );
  }
}
