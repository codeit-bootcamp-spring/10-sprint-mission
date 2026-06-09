package com.sprint.mission.discodeit.service.basic;

import static java.util.stream.Collectors.toSet;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.NotAllowedInPrivateChannelException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import com.sprint.mission.discodeit.service.cache.ChannelCacheService;
import com.sprint.mission.discodeit.util.UserSessionManager;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;//공개채널에 멤버추가를 위한 의존성
  private final ChannelMapper channelMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final UserSessionManager userSessionManager;
  private final ChannelCacheService channelCacheService;

  @Override
  @CacheEvict(value = "channelListCache", key = "'public'")
  public ChannelDto create(PublicChannelCreateRequest dto) {
    log.debug("공개 채널 생성 시도: channelName={}", dto.name());
    Channel channel = channelMapper.toEntity(dto);
    log.debug("공개 채널 생성 중: 레포지토리 저장 시도");
    channelRepository.save(channel);
    //모든 사용자가 멤버!
    log.debug("공개 채널 생성 중: 모든 사용자 조회 및 읽기 상태 생성&저장");
    List<User> allUsers = userRepository.findAllFetchUserInfo();
    List<ReadStatus> readStatuses = allUsers.stream()
        .map(u -> ReadStatus.create(u, channel, Instant.EPOCH))
        .toList();
    readStatusRepository.saveAll(readStatuses);
    log.info("공개 채널 생성 성공: channelId={}", channel.getId());
    return channelMapper.toDto(channel, allUsers, null, getOnlineUserIds());
  }

  @Override
  public ChannelDto create(PrivateChannelCreateRequest dto) {
    log.debug("비공개 채널 생성 시도: channelMembers={}", dto.memberIds());
    Channel channel = channelMapper.toEntity(dto);
    log.debug("비공개 채널 생성 중: 레포지토리 저장 시도");
    channelRepository.save(channel);
    log.debug("비공개 채널 생성 중: 요청 멤버 조회, memberIds={}", dto.memberIds());
    List<User> members = userRepository.findAllByIdFetchUserInfo(dto.memberIds());//쿼리 한번으로 조회
    if (members.size() != dto.memberIds().size()) {//멤버가 전부 유저가 아닐때만
      throw new UserNotFoundException().addDetail("requestMemberIds", dto.memberIds())
          .addDetail("validMemberSize", members.size());
    }
    log.debug("비공개 채널 생성 중: 멤버에 대한 읽기상태 생성 및 저장");
    List<ReadStatus> readStatuses = members.stream()
        .map(u -> ReadStatus.create(u, channel, Instant.EPOCH))
        .toList();
    readStatusRepository.saveAll(readStatuses);
    log.info("비공개 채널 생성 성공: channelId={}", channel.getId());
    channelCacheService.removePrivateChannelCaches(Set.copyOf(dto.memberIds()));//해당 멤버 개인채널캐시 삭젠
    return channelMapper.toDto(channel, members,
        null, getOnlineUserIds());
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    Channel channel = get(channelId);
    return channelMapper.toDto(channel, getMembers(channelId), getLastMessageAt(channelId),
        getOnlineUserIds());
  }

  //채널목록은 캐시값 사용 + 마지막 메시지, 현재 온라인 유저는 실시간
  @Override
  @Transactional(readOnly = true)
  public List<ChannelDto> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException().addDetail("userId", userId);
    }
    //List<ChannelDto> response = new ArrayList<>();
        /*
            사용자가 속한 채널 = 비공개+공개
         */
    //기존로직 - n+1 발생
//    readStatusRepository.findAllByUserId(userId)
//        .forEach(r -> {
//          response.add(channelMapper.toDto(r.getChannel()));
//        });
    //변경로직
    /*
    Map<UUID, List<User>> userMap = new HashMap<>();
    Map<UUID, Channel> myChannels = new HashMap<>();
    readStatusRepository.findAllByUserIdFetchChannel(userId)
        .forEach(r -> {
          userMap.putIfAbsent(r.getChannel().getId(), new ArrayList<>());
          myChannels.put(r.getChannel().getId(), r.getChannel());
        });
    readStatusRepository.findAllByChannelIdInFetchUser(userMap.keySet())
        .forEach(r -> {
          userMap.get(r.getChannel().getId()).add(r.getUser());
        });

     */

    List<ChannelDto> cachedPublicChannelDtos = channelCacheService.getPublicChannelsWithoutLastMessageAtAndOnline();
    List<ChannelDto> cachedPrivateChannelDtos = channelCacheService.getPrivateChannelsWithoutLastMessageAtAndOnline(
        userId);

    List<ChannelDto> allChannelDtos = new ArrayList<>();
    allChannelDtos.addAll(cachedPublicChannelDtos);
    allChannelDtos.addAll(cachedPrivateChannelDtos);

    if (allChannelDtos.isEmpty()) {
      return Collections.emptyList();
    }

    Set<UUID> channelIds = allChannelDtos.stream().map(ChannelDto::id).collect(toSet());

    Map<UUID, Instant> lastMessages = new HashMap<>();
    messageRepository.findAllLastMessagesByChannelId(channelIds)
        .forEach(m -> {
          lastMessages.put(m.channelId(), m.maxCreatedAt());
        });

    Set<UUID> onlineUserIds = userSessionManager.getOnlineUserIds();

    /*
    myChannels.values().forEach(c -> {
      response.add(channelMapper.toDto(c, userMap.get(c.getId()),
          lastMessages.getOrDefault(c.getId(), null), onlineUserIds));
    });

    return response.stream()
        .sorted(Comparator.comparing(ChannelDto::createdAt))
        .toList();//채널 순서 보장

     */

    return allChannelDtos.stream()
        .map(c -> channelMapper.toDto(
            c,
            c.participants(),
            lastMessages.getOrDefault(c.id(), null),
            onlineUserIds)
        ).sorted(Comparator.comparing(ChannelDto::createdAt))
        .toList();
  }

  @Override
  @CacheEvict(value = "channelListCache", key = "'public'")
  public ChannelDto update(UUID id, PublicChannelUpdateRequest dto) {
    log.debug("채널 수정 시도: channelId={}", id);
    Channel channel = get(id);
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new NotAllowedInPrivateChannelException().addDetail("channelId", id);
    }
    channel.update(dto.name(), dto.description());
    log.info("공개 채널 수정 성공: channelId={}", channel.getId());
    return channelMapper.toDto(channel, getMembers(id), getLastMessageAt(id), getOnlineUserIds());
  }

  @Override
  public void delete(UUID channelId) {
    log.debug("채널 삭제 시도: channelId={}", channelId);
    Channel channel = get(channelId);
    log.debug("채널 삭제 중: 채널 메세지의 첨부파일 제거, channelId={}", channelId);
    binaryContentRepository.bulkDeleteByChannelId(channelId);//첨부파일 삭제시 연관테이블은 자동삭제
    log.debug("채널 삭제 중: 채널 메세지 삭제, channelId={}", channelId);
    messageRepository.bulkDeleteByChannelId(channelId);//메세지 삭제
    channelRepository.deleteById(channelId);
    //readStatusRepository.bulkDeleteByChannelId(channelId);데베 설정으로 자동 삭제
    if (channel.getType() == ChannelType.PUBLIC) {
      channelCacheService.removePublicChannelCaches();
    } else {
      channelCacheService.removeAllCaches();
    }
    log.info("채널 삭제 성공: channelId={}", channelId);
  }


  private Channel get(UUID channelId) {
    return channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException().addDetail("channelId", channelId));
  }

  private List<User> getMembers(UUID channelId) {
    return readStatusRepository.findAllByChannelIdFetchUser(channelId).stream()
        .map(ReadStatus::getUser)
        .toList();
  }

  private Instant getLastMessageAt(UUID channelId) {
    return messageRepository.findLastMessageByChannelId(channelId)
        .map(Message::getCreatedAt)
        .orElse(null);
  }

  private Set<UUID> getOnlineUserIds() {
    return userSessionManager.getOnlineUserIds();
  }
}
