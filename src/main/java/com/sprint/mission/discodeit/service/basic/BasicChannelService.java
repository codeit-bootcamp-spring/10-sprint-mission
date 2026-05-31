package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.FieldNotValidException;
import com.sprint.mission.discodeit.exception.RequestNullException;
import com.sprint.mission.discodeit.exception.channel.ChannelNameDuplicationException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelParticipantListEmptyException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional
  @PreAuthorize("hasRole('CHANNEL_MANAGER') or hasRole('ADMIN')")
  public ChannelDto createPublicChannel(PublicChannelCreateDTO req) {

    if (req == null) {
      throw new RequestNullException();
    }

    // 채널 생성 메서드 시작 로그
    log.trace("[Channel] 공개 채널 생성 메서드 시작: channelname={}", req.name());

    // 채널명 중복 검증 및 예외 던지기
    if (channelRepository.existsByName(req.name())) {
      throw new ChannelNameDuplicationException(req.name());
    }

    // 채널 객체 생성 (영속화는 아직)
    Channel channel = new Channel(ChannelType.PUBLIC, req.name(), req.description());

    // 생성된 채널 객체 정보 로그
    log.debug("[Channel] 생성된 공개 채널 객체 정보: channelId={}", channel.getId());

    // 생성된 채널 객체 영속화 및 dto 변환 리턴 시도
    log.trace("[Channel] 생성된 공개 채널 영속화 시도: channelId={}", channel.getId());

    Channel saved = channelRepository.save(channel);
    log.info("[Channel] 공개 채널 생성 및 영속화 완료: channelId={}", saved.getId());

    return channelMapper.toDto(saved, null);
  }

  @Transactional
  @Override
  public ChannelDto createPrivateChannel(PrivateChannelCreateDTO req) {
    // 사설 채널 생성 메서드 시작 로그
    log.trace("[Channel] 사설 채널 생성 메서드 시작");

    if (req == null || req.users() == null) {
      throw new RequestNullException();
    }

    // 사설 채널 유저 리스트를 생성 (Null 요소 제외 및 중복 유저 제거)
    List<UUID> participantIds = req.users().stream()
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    // 유저 리스트가 비어있으면 예외 발생
    if (participantIds.isEmpty()) {
      throw new ChannelParticipantListEmptyException();
    }

    log.trace("사설 채널 객체 생성 시도");
    String privateChannelName = "private-" + UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PRIVATE, privateChannelName, "private channel");
    log.debug("생성된 사설 채널 객체 정보: channelId={}", channel.getId());

    log.trace("생성된 사설 채널 객체 영속화 시도: channelId={}", channel.getId());
    Channel saved = channelRepository.save(channel);
    log.debug("사설 채널 객체 영속화 정보: channelId={}", saved.getId());

    log.trace("유저 리스트 순회하며 ReadStatus 최신화 시도");
    participantIds.forEach(u -> {
      ReadStatus rs = new ReadStatus(userRepository.findById(u)
          .orElseThrow(() -> new UserNotFoundException(u)), saved);
      readStatusRepository.save(rs);
    });

    // ReadStatus가 최신화된 채널을 다시 영속화, 변동사항 없으면 이전에 영속화했던 saved를 대입
    Channel savedWithParticipants = channelRepository.findWithParticipantsById(saved.getId())
        .orElse(saved);

    log.info("사설 채널 생성 및 영속화 성공");

    return channelMapper.toDto(savedWithParticipants, null);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    // 채널 조회 메서드 시작 TRACE 로그
    log.trace("[Channel] 채널 조회 메서드 시작: channelId={}", channelId);

    // 채널 조회 유틸 메서드 호출
    Channel channel = getChannel(channelId);

    Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId);

    log.info("채널 조회 성공: channelName={}", channel.getName());
    return channelMapper.toDto(channel, lastMessage);
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {

    if (userId == null) {
      throw new FieldNotValidException("userId");
    }

    log.trace("유저 ID를 통해 채널 조회 메서드 시작: userId={}", userId);

    List<Channel> channels = channelRepository.findAllVisibleWithParticipants(
        userId, ChannelType.PUBLIC);

    List<UUID> channelIds = channels.stream()
        .map(Channel::getId)
        .toList();

    Map<UUID, Message> lastMessageMap = messageRepository.findLastMessageByChannelIds(
            channelIds)
        .stream()
        .collect(Collectors.toMap(
            m -> m.getChannel().getId(),
            m -> m,
            (m1, m2) -> m1
        ));

    List<ChannelDto> result = channels.stream()
        .map(channel -> channelMapper.toDto(
            channel, lastMessageMap.get(channel.getId())
        )).toList();

    log.info("채널 조회 성공: channelNames={}", result
        .stream()
        .map(ChannelDto::name)
        .toList()
    );

    return result;
  }


  @Override
  @Transactional
  @PreAuthorize("hasRole('CHANNEL_MANAGER') or hasRole('ADMIN')")
  public ChannelDto updatePublicChannel(UUID channelId, PublicChannelUpdateRequestDTO req) {
    if (channelId == null) {
      throw new FieldNotValidException("channelId");
    }

    if (req == null) {
      throw new RequestNullException();
    }

    // 채널 업데이트 메서드 시작 로그
    log.trace("채널 업데이트 메서드 시작: channelId={}, newName={}, newDescription={}",
        channelId, req.newName(), req.newDescription());

    // 채널 레포지토리에서 수정하고자 하는 채널이 존재하는지 조회
    Channel channel = getChannel(channelId);
    if (channel.getType() != ChannelType.PUBLIC) {
      throw new ChannelNotFoundException(channelId);
    }

    // 채널 엔티티의 update 메서드를 통해 수정
    channel.update(req.newName(), req.newDescription()); // dirty-checking

    // 수정하고자 하는 채널의 가장 최신 메시지를 찾음.
    Message lastMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId);

    log.info("채널 수정 완료: channelNewName={}, channelNewDescription={}",
        channel.getName(), channel.getDescription());

    return channelMapper.toDto(channel, lastMessage);
  }

  @Override
  @Transactional
  public ChannelDto updatePrivateChannel(UUID channelId, PublicChannelUpdateRequestDTO req) {
    if (channelId == null) {
      throw new FieldNotValidException("channelId");
    }
    Channel channel = getChannel(channelId);
    if (channel.getType() != ChannelType.PRIVATE) {
      throw new ChannelNotFoundException(channelId);
    }
    throw new PrivateChannelUpdateException(channelId);
  }

  @Transactional
  public List<ChannelDto> findAll() {
    // 공개 채널 전체 조회 메서드 시작 로그
    log.trace("채널 전체 조회 메서드 시작");

    // 채널 레포지토리에서 전체 채널 조회
    List<Channel> channels = channelRepository.findAll();

    // 메세지 레포지토리에서 채널 ID를 사용하여 각 채널 당 최신 메시지를 추출 및 리스트화
    List<Message> lastMessages = messageRepository.findLastMessageByChannelIds(
        channels.stream().map(Channel::getId).toList());

    // Map 자료구조를 사용,
    Map<UUID, Message> lastMessageMap = lastMessages
        .stream()
        .collect(Collectors.toMap( // 최신 메시지 리스트를 Map 구조로 변환
            m -> m.getChannel().getId(), // 메시지 별 채널 Id가 Key
            m -> m, // 해당 최신 메시지가 Value가 됨.
            (m1, m2) -> m1
        ));

    log.info("채널 전체 조회 성공");

    // 해당 Map을 사용하여 채널 응답 Dto로 변환 및 응답
    return channels.stream()
        .map(channel -> channelMapper.toDto(
            channel, lastMessageMap.get(channel.getId())
        )).toList();
  }


  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER') or hasRole('ADMIN')")
  public void deletePublicChannel(UUID channelId) {
    // 채널 삭제 메서드 시작 로그
    log.trace("공개 채널 삭제 메서드 시작: channelId={}", channelId);

    // 채널 조회
    Channel channel = getChannel(channelId);
    if (channel.getType() != ChannelType.PUBLIC) {
      throw new ChannelNotFoundException(channelId);
    }

    log.debug("[Channel] 삭제 할 채널 정보: channelId={}", channelId);

    // 채널 삭제
    channelRepository.delete(channel);

    log.info("공개 채널 삭제 성공: channelName={}", channel.getName());
  }

  @Transactional
  @Override
  public void deletePrivateChannel(UUID channelId) {
    log.trace("비공개 채널 삭제 메서드 시작: channelId={}", channelId);

    Channel channel = getChannel(channelId);
    if (channel.getType() != ChannelType.PRIVATE) {
      throw new ChannelNotFoundException(channelId);
    }

    log.debug("[Channel] 삭제 할 비공개 채널 정보: channelId={}", channelId);
    channelRepository.delete(channel);
    log.info("비공개 채널 삭제 성공: channelName={}", channel.getName());
  }

  // 채널 ID로 채널을 반환하고 없으면 예외를 반환하는 유틸 메서드
  private Channel getChannel(UUID id) {
    return channelRepository.findById(id).orElseThrow(
        () -> new ChannelNotFoundException(id)
    );
  }
}
