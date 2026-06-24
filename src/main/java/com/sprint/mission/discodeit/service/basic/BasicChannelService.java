package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.ChannelChangedEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CacheEvict(value = "channels", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    public ChannelDto createPublic(CreatePublicChannelRequestDto request) {
        log.info("Public 채널 생성 요청: name={}", request.name());

        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);

        log.info("Public 채널 생성 성공: channelId={}", savedChannel.getId());
        ChannelDto dto = channelMapper.toDto(savedChannel);
        eventPublisher.publishEvent(new ChannelChangedEvent("created", dto));
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = "channels", allEntries = true)
    public ChannelDto createPrivate(CreatePrivateChannelRequestDto request) {
        log.debug("Private 채널 생성 요청");
        // 명세에 name이 없으므로 서버에서 기본 이름 자동 생성
        String defaultName = "Private";
        Channel channel = new Channel(ChannelType.PRIVATE, defaultName, null);
        channelRepository.save(channel);

        if (request.participantIds() != null) {
            for (UUID userId : request.participantIds()) {
                User user = userRepository.findById(userId).orElseThrow();
                ReadStatus status = new ReadStatus(user, channel);
                readStatusRepository.save(status);
            }
        }
        log.info("Private 채널 생성 성공: channelId={}", channel.getId());
        ChannelDto dto = channelMapper.toDto(channel);
        eventPublisher.publishEvent(new ChannelChangedEvent("created", dto));
        return dto;
    }

    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = getChannelEntity(channelId);
        return channelMapper.toDto(channel);
    }

    @Override
    @Cacheable(value = "channels", key = "#userId")
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> channels = channelRepository.findAccessibleChannelsByUserId(userId);
        return mapChannelsToDtos(channels);
    }

    @Override
    public List<ChannelDto> findAll() {
        List<Channel> channels = channelRepository.findAll();
        return mapChannelsToDtos(channels);
    }

    @Override
    @Transactional
    @CacheEvict(value = "channels", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    public ChannelDto update(UUID channelId, UpdateChannelRequestDto request) {
        log.debug("채널 정보 수정 요청: channelId={}", channelId);
        Channel channel = getChannelEntity(channelId);

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new PrivateChannelUpdateException(channelId);
        }
        channel.update(request.newName(), request.newDescription());

        log.info("채널 정보 수정 완료: channelId={}", channel.getId());
        ChannelDto dto = channelMapper.toDto(channel);
        eventPublisher.publishEvent(new ChannelChangedEvent("updated", dto));
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = "channels", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    public void delete(UUID channelId) {
        log.warn("채널 삭제 요청: channelId={}", channelId);

        Channel channel = getChannelEntity(channelId);
        ChannelDto dto = channelMapper.toDto(channel); // 참여자 포함 DTO를 삭제 전 캡처
        readStatusRepository.deleteAllByChannelId(channelId);
        messageRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
        eventPublisher.publishEvent(new ChannelChangedEvent("deleted", dto));

        log.info("채널 삭제 완료: channelId={}", channelId);
    }

    // 채널 엔티티 조회
    private Channel getChannelEntity(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private List<ChannelDto> mapChannelsToDtos(List<Channel> channels) {
        if (channels.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> channelIds = channels.stream().map(Channel::getId).toList();

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdIn(channelIds);
        Map<UUID, List<UserDto>> participantsMap = readStatuses.stream()
                .collect(Collectors.groupingBy(
                        rs -> rs.getChannel().getId(),
                        Collectors.mapping(rs -> userMapper.toDto(rs.getUser(),
                                jwtRegistry.hasActiveJwtInformationByUserId(rs.getUser().getId())), Collectors.toList())
                ));

        Map<UUID, Instant> lastMessageMap = new HashMap<>();
        List<MessageRepository.ChannelLastMessageTime> lastMessageTimes =
            messageRepository.findLastMessageTimesByChannelIds(channelIds);

        for (MessageRepository.ChannelLastMessageTime lt : lastMessageTimes) {
            lastMessageMap.put(lt.getChannelId(), lt.getLastMessageTime());
        }

        return channels.stream()
            .map(channel -> channelMapper.toDtoWithContext(channel, lastMessageMap, participantsMap))
            .toList();
    }


}
