package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.ChannelSummary;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.DuplicateNameException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelNotEditableException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final UserMapper userMapper;

  @Transactional
  @Override
  public ChannelDto createChannel(ChannelDto.PrivateChannelCreateRequest channelPrivateReq) {
    log.debug("[Service] 비공개채널 생성 시작: participants={}", channelPrivateReq.participantIds());
    List<UUID> participantIds = Optional.ofNullable(channelPrivateReq.participantIds())
        .orElse(new ArrayList<>());

    // 참여자 목록의 유저가 user DB에 있는지 확인
    List<User> users = userRepository.findAllById(participantIds);
    if (participantIds.size() != users.size()) {
      throw new UserNotFoundException();
    }

    Channel privateChannel = Channel.of(participantIds);
    channelRepository.save(privateChannel);
    log.debug("[Service] 비공개채널 저장 완료: id={}", privateChannel.getId());

    // ReadStatus 생성
    List<ReadStatus> readStatuses = users.stream()
        .map(user -> new ReadStatus(user, privateChannel)).toList();
    readStatusRepository.saveAll(readStatuses);
    log.debug("[Service] ReadStatuses 저장 완료: channelId={}", privateChannel.getId());

    log.info("[Service] 비공개채널 생성 성공: id={}", privateChannel.getId());
    return toResponse(privateChannel);
  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public ChannelDto createChannel(ChannelDto.PublicChannelCreateRequest channelPublicReq) {
    log.debug("[Service] 공개채널 생성 시작: name={}, description={}",
        channelPublicReq.name(), channelPublicReq.description());
    validateDuplicateName(channelPublicReq.name());

    Channel publicChannel = Channel.of(channelPublicReq.name(), channelPublicReq.description());
    channelRepository.save(publicChannel);
    log.debug("[Service] 공개채널 저장 완료: id={}", publicChannel.getId());

    log.info("[Service] 공개채널 생성 성공: id={}, name={}",
        publicChannel.getId(), publicChannel.getName());
    return toResponse(publicChannel);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    getUserOrThrow(userId);

    // PUBLIC 채널 전부 + userId가 참여한 PRIVATE 채널(JPQL 처리)
    List<ChannelSummary> channels = channelRepository.findAllByUserId(userId);
    List<UUID> privateChannelsId = channels.stream()
        .filter(s -> s.type() == ChannelType.PRIVATE)
        .map(ChannelSummary::id).toList();

    Map<UUID, List<UserDto>> participants = readStatusRepository.findAllByChannelIdIn(
            privateChannelsId)
        .stream()
        .collect(Collectors.groupingBy(
            rs -> rs.getChannel().getId(),    // 채널 UUID 기반으로 그룹핑
            Collectors.mapping(rs -> userMapper.toDto(rs.getUser()), Collectors.toList())
        ));

    return channelMapper.toDto(channels, participants);
  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public ChannelDto updateChannel(UUID uuid, ChannelDto.PublicChannelUpdateRequest channelReq) {
    log.debug("[Service] 채널 수정 시작: id={}, newName={}, newDescription={}",
        uuid, channelReq.newName(), channelReq.newDescription());
    Channel channel = getChannelOrThrow(uuid);

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelNotEditableException();
    }

    // name 중복성 검사
    if (channelReq.newName() != null && !Objects.equals(channel.getName(), channelReq.newName())) {
      validateDuplicateName(channelReq.newName());
    }

    Optional.ofNullable(channelReq.newName())
        .filter(StringUtils::hasText)
        .ifPresent(channel::updateName);
    Optional.ofNullable(channelReq.newDescription())
        .filter(StringUtils::hasText)
        .ifPresent(channel::updateDescription);
    channelRepository.save(channel);
    log.debug("[Service] 수정된 채널 저장 완료: id={}", channel.getId());

    log.info("[Service] 채널 수정 성공: id={}, name={}, description={}",
        channel.getId(), channel.getName(), channel.getDescription());
    return toResponse(channel);
  }

  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @Transactional
  @Override
  public void deleteChannel(UUID uuid) {
    log.debug("[Service] 채널 삭제 시작: id={}", uuid);
    getChannelOrThrow(uuid);

    channelRepository.deleteById(uuid);
    log.info("[Service] 채널 삭제 성공: id={}", uuid);
  }

  private void validateDuplicateName(String name) {
    if (channelRepository.existsByName(name)) {
      throw new DuplicateNameException();
    }
  }

  private Channel getChannelOrThrow(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException());
  }

  private User getUserOrThrow(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException());
  }

  private ChannelDto toResponse(Channel channel) {
    return channelMapper.toDto(channel);
  }
}
