package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelAlreadyExistsException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelParticipantException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 채널 관련 비즈니스 로직을 처리하는 기본 서비스 클래스입니다.
 * 공개 채널(PUBLIC)과 비공개 채널(PRIVATE)의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 새로운 공개 채널을 생성합니다.
     *
     * @param request 공개 채널 생성 요청 정보
     * @return 생성된 채널의 상세 정보
     */
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public ChannelDto.Response create(ChannelDto.PublicChannelCreateRequest request) {
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        Channel savedChannel = channelRepository.save(channel);

        log.info("[Channel] 공개 채널 생성 완료: ID={}, Name={}", savedChannel.getId(), savedChannel.getName());
        
        // 캐시 무효화 및 부가 처리를 위한 이벤트 발행
        eventPublisher.publishEvent(new ChannelEvents.Created(savedChannel.getId(), ChannelType.PUBLIC, List.of()));
        
        return toDto(savedChannel);
    }

    /**
     * 새로운 비공개 채널을 생성하고 참여자들을 등록합니다.
     *
     * @param request 비공개 채널 생성 요청 정보 (참여자 ID 목록 포함)
     * @return 생성된 채널의 상세 정보
     */
    @Override
    @Transactional
    public ChannelDto.Response create(ChannelDto.PrivateChannelCreateRequest request) {
        List<User> participants = validateAndGetParticipants(request.participantIds());

        // 중복 체크: 이미 동일한 구성원의 비공개 채널이 존재하는지 확인
        List<Channel> existingChannels = channelRepository.findPrivateChannelByParticipants(
            request.participantIds(), 
            (long) request.participantIds().size()
        );

        if (!existingChannels.isEmpty()) {
            throw PrivateChannelAlreadyExistsException.withParticipantIds(new HashSet<>(request.participantIds()));
        }

        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        Channel savedChannel = channelRepository.save(channel);

        // 비공개 채널 참여자 정보 저장 (ReadStatus)
        participants.forEach(user -> 
            readStatusRepository.save(new ReadStatus(user, savedChannel, savedChannel.getCreatedAt()))
        );

        log.info("[Channel] 비공개 채널 생성 완료: ID={}, Participants={}", savedChannel.getId(), participants.size());

        // 비공개 채널의 경우 참여자들의 캐시만 만료시키도록 이벤트 발행
        eventPublisher.publishEvent(new ChannelEvents.Created(savedChannel.getId(), ChannelType.PRIVATE, new ArrayList<>(request.participantIds())));

        return toDto(savedChannel);
    }

    /**
     * 특정 채널을 ID로 조회합니다.
     *
     * @param channelId 조회할 채널 ID
     * @return 채널 상세 정보
     * @throws ChannelNotFoundException 채널이 존재하지 않을 경우
     */
    @Override
    public ChannelDto.Response find(UUID channelId) {
        return channelRepository.findById(channelId)
                .map(this::toDto)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
    }

    /**
     * 특정 사용자가 접근할 수 있는 모든 채널 목록을 조회합니다.
     * 결과는 사용자별 캐시에 저장됩니다.
     *
     * @param userId 사용자 ID
     * @return 접근 가능한 채널 목록
     */
    @Override
    @Cacheable(value = "userChannelsCache", key = "#userId")
    public List<ChannelDto.Response> findAllByUserId(UUID userId) {
        validateUserExists(userId);
        List<Channel> channels = channelRepository.findAllAccessibleByUserId(userId);

        log.debug("[Channel] 사용자 채널 목록 조회: UserId={}, Count={}", userId, channels.size());
        return toDtos(channels);
    }

    /**
     * 시스템에 존재하는 모든 채널 목록을 조회합니다.
     *
     * @return 전체 채널 목록
     */
    @Override
    public List<ChannelDto.Response> findAll() {
        List<Channel> channels = channelRepository.findAll();
        return toDtos(channels);
    }

    /**
     * 공개 채널의 정보를 수정합니다.
     *
     * @param channelId 수정할 채널 ID
     * @param request 수정할 채널 정보
     * @return 수정된 채널 상세 정보
     */
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public ChannelDto.Response update(UUID channelId, ChannelDto.UpdatePublicRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        if (channel.getType() == ChannelType.PRIVATE) {
            throw PrivateChannelUpdateNotAllowedException.withId(channelId);
        }

        channel.update(request.newName(), request.newDescription());
        
        log.info("[Channel] 채널 정보 수정 완료: ID={}, NewName={}", channelId, request.newName());

        eventPublisher.publishEvent(new ChannelEvents.Updated(channelId, ChannelType.PUBLIC, List.of()));

        return toDto(channel);
    }

    /**
     * 특정 채널을 삭제합니다.
     *
     * @param channelId 삭제할 채널 ID
     */
    @Override
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Transactional
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        ChannelType type = channel.getType();
        List<UUID> participantIds = new ArrayList<>();

        if (type == ChannelType.PRIVATE) {
            // 삭제 전 참여자 명단을 확실히 확보
            participantIds = readStatusRepository.findParticipantIdsByChannelId(channelId);
            log.debug("[Channel] 비공개 채널 삭제 전 참여자 확보: Count={}", participantIds.size());
        }

        channelRepository.delete(channel);
        channelRepository.flush(); // DB 반영 강제하여 정합성 확보
        
        log.info("[Channel] 채널 삭제 완료: ID={}, Type={}", channelId, type);

        // 캐시 무효화 이벤트 발행
        // 만약 비공개 채널인데 참여자가 0명으로 조회되었다면 안전을 위해 전체 캐시 무효화(fallback) 시도 가능
        eventPublisher.publishEvent(new ChannelEvents.Deleted(channelId, type, participantIds));
    }

    // --- Private Helpers ---

    private List<User> validateAndGetParticipants(Collection<UUID> participantIds) {
        List<User> participants = userRepository.findAllById(participantIds);
        
        if (participants.size() != participantIds.size()) {
            Set<UUID> foundIds = participants.stream().map(User::getId).collect(Collectors.toSet());
            Set<UUID> missingIds = participantIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());
            throw UserNotFoundException.withIds(missingIds);
        }

        if (participants.size() < 2) {
            throw PrivateChannelParticipantException.minimumParticipants(participants.size());
        }
        return participants;
    }

    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }
    }

    private ChannelDto.Response toDto(Channel channel) {
        List<UserDto.Response> participants = new ArrayList<>();
        if (channel.getType() == ChannelType.PRIVATE) {
            participants = readStatusRepository.findAllByChannelId(channel.getId())
                    .stream()
                    .map(ReadStatus::getUser)
                    .map(userMapper::toResponse)
                    .toList();
        }
        return channelMapper.toResponse(channel, participants);
    }

    private List<ChannelDto.Response> toDtos(List<Channel> channels) {
        if (channels.isEmpty()) return List.of();

        List<UUID> channelIds = channels.stream().map(Channel::getId).toList();
        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdsWithUser(channelIds);

        Map<UUID, List<UserDto.Response>> participantsByChannel = readStatuses.stream()
                .collect(Collectors.groupingBy(
                        rs -> rs.getChannel().getId(),
                        Collectors.mapping(rs -> userMapper.toResponse(rs.getUser()), Collectors.toList())
                ));

        return channels.stream()
                .map(channel -> channelMapper.toResponse(
                        channel,
                        participantsByChannel.getOrDefault(channel.getId(), List.of())
                ))
                .toList();
    }
}
