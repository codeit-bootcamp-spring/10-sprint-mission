package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicChannelService implements ChannelService {
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;

  @Override
  public Channel create(PublicChannelCreateRequest request) {
    log.info("공개 채널 생성 요청 - name={}", request.name());

    Channel channel = new Channel(
            ChannelType.PUBLIC,
            request.name(),
            request.description()
    );

    Channel saved = channelRepository.save(channel);

    log.info("공개 채널 생성 완료 - channelId={}", saved.getId());

    return saved;
  }

  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    log.info("비공개 채널 생성 요청 - participantCount={}", request.participantIds().size());

    Channel channel = new Channel(ChannelType.PRIVATE, "", "");
    Channel createdChannel = channelRepository.save(channel);

    List<User> participants = request.participantIds().stream()
            .map(userId -> userRepository.findById(userId)
                    .orElseThrow(() -> {
                      log.warn("비공개 채널 생성 실패 - 참여자 없음 userId={}", userId);
                      return new NoSuchElementException("User with id " + userId + "not found");
                    }))
            .toList();

    List<ReadStatus> readStatuses = participants.stream()
            .map(user -> new ReadStatus(user, createdChannel, Instant.now()))
            .toList();

    readStatusRepository.saveAll(readStatuses);

    log.info("비공개 채널 생성 완료 - channelId={}, participantCount={}", createdChannel.getId(), participants.size());

    return createdChannel;
  }

  @Transactional(readOnly = true)
  @Override
  public Channel find(UUID channelId) {
    log.debug("채널 조회 요청 - channelId={}", channelId);

    return channelRepository.findById(channelId)
            .orElseThrow(() -> {
              log.warn("채널 조회 실패 - 존재하지 않음 channelId={}", channelId);
              return new NoSuchElementException("Channel with id " + channelId + " not found");
            });
  }

  @Transactional(readOnly = true)
  @Override
  public List<Channel> findAllByUser_Id(UUID userId) {
    log.debug("사용자 채널 목록 조회 요청 - userId={}", userId);

    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
            .map(readStatus -> readStatus.getChannel().getId())
            .toList();

    List<Channel> channels = channelRepository.findAll().stream()
            .filter(channel ->
                    (channel.getType() == ChannelType.PUBLIC)
                            || mySubscribedChannelIds.contains(channel.getId())
            )
            .toList();

    log.debug("사용자 채널 목록 조회 완료 - userId={}, count={}", userId, channels.size());

    return channels;
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    log.info("채널 수정 요청 - channelId={}", channelId);

    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
              log.warn("채널 수정 실패 - 존재하지 않음 channelId={}", channelId);
              return new NoSuchElementException("Channel with id " + channelId + " not found");
            });

    if (channel.getType() == ChannelType.PRIVATE) {
      log.warn("채널 수정 실패 - 비공개 채널 수정 불가 channelId={}", channelId);
      throw new IllegalArgumentException("Private channel cannot be updated");
    }

    channel.update(request.newName(), request.newDescription());

    log.info("채널 수정 완료 - channelId={}", channelId);

    return channel;
  }

  @Override
  public void delete(UUID channelId) {
    log.info("채널 삭제 요청 - channelId={}", channelId);

    Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> {
              log.warn("채널 삭제 실패 - 존재하지 않음 channelId={}", channelId);
              return new NoSuchElementException("Channel with id " + channelId + " not found");
            });

    messageRepository.deleteAllByChannel_Id(channel.getId());
    readStatusRepository.deleteAllByChannel_Id(channel.getId());
    channelRepository.delete(channel);

    log.info("채널 삭제 완료 - channelId={}", channelId);
  }
}
