package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.ChannelChangeEvent;
import com.sprint.mission.discodeit.event.PrivateChannelChangeEvent;
import com.sprint.mission.discodeit.event.enums.ChangeType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelCannotBeUpdatedException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelParticipantRequiredException;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.common.NoChangeValueException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final ChannelMapper channelMapper;
    private final UserMapper userMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    @CacheEvict(value = "channelList", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Override
    public ChannelDto createPublicChannel(PublicChannelCreateRequest request) {
        log.debug("[PUBLIC_CHANNEL_CREATE] 공개 채널 생성 시작: name={}, description={}",
                request.name(), request.description());

        Channel channel = new Channel(
                ChannelType.PUBLIC,
                request.name(),
                request.description()
        );
        channelRepository.save(channel);

        log.info("[PUBLIC_CHANNEL_CREATE] 공개 채널 생성 완료: channelId={}, type={}, name={}, description={}",
                channel.getId(), channel.getType(), channel.getName(), channel.getDescription());

        ChannelDto channelDto = channelMapper.toDto(channel);

        changeEventPublish(channel.getType(), ChangeType.CREATED, channelDto);

        return channelDto;
    }

    @CacheEvict(value = "channelList", allEntries = true)
    @Override
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequest request) {
        log.debug("[PRIVATE_CHANNEL_CREATE] 비공개 채널 생성 시작: count={}",
                request.participantIds() != null ? request.participantIds().size() : 0);

        if (request.participantIds() == null || request.participantIds().isEmpty()) {
            throw new PrivateChannelParticipantRequiredException();
        }
        // 인원 검증
        List<User> participants = request.participantIds().stream()
                .map(id -> validateAndGetUserByUserId(id))
                .toList();

        // PRIVATE 채널은 channelName과 channelDescription이 null
        Channel channel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);

        Instant now = Instant.now();
        participants.forEach(participant -> {
            ReadStatus participantReadStatus = new ReadStatus(participant, channel, now, true);
            readStatusRepository.save(participantReadStatus);
        });

        log.info("[PRIVATE_CHANNEL_CREATE] 비공개 채널 생성 완료: channelId={}, type={}, count={}",
                channel.getId(), channel.getType(), participants.size());

        ChannelDto channelDto = channelMapper.toDto(channel);

        changeEventPublish(channel.getType(), ChangeType.CREATED, channelDto);

        return channelDto;
    }

    @Transactional(readOnly = true)
    @Override
    public ChannelDto find(UUID channelId) {
        log.debug("[CHANNEL_FIND] 채널 조회 시작: channelId={}", channelId);

        // Channel ID null 검증
        Channel channel = validateAndGetChannelByChannelId(channelId);

        log.debug("[CHANNEL_FIND] 채널 조회 완료: channelId={}, type={}, name={}, description={}",
                channel.getId(), channel.getType(), channel.getName(), channel.getDescription());

        return channelMapper.toDto(channel);
    }

    @Cacheable(value = "channelList", key = "#userId", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        log.debug("[CHANNEL_LIST_FIND] 채널 목록 조회 시작: userId={}", userId);

        // User ID null 검증
        validateAndGetUserByUserId(userId);

        // 접근 가능한 전체 채널 조회
        List<Channel> channels = channelRepository.findChannelByUserId(ChannelType.PUBLIC, userId);

        // 접근 가능한 전체 채널 ID
        List<UUID> channelIds = channels.stream()
                .map(channel -> channel.getId())
                .toList();

        // 접근 가능한 Private 채널 ID
        List<UUID> privateChannelIds = channels.stream()
                .filter(channel -> channel.getType().equals(ChannelType.PRIVATE))
                .map(channel -> channel.getId())
                .toList();

        // 각 채널의 마지막 메시지 createdAt 시간
        Map<UUID, Instant> lastMessageAtMap =
                messageRepository.findLastMessageAtDtoByChannelIds(channelIds)
                        .stream()
                        .collect(Collectors.toMap(
                                        dto -> dto.id(),
                                        dto -> dto.lastMessageAt()
                                )
                        );

        // 채널별 참가자 목록 조회
        Map<UUID, List<UserDto>> participantMap =
                readStatusRepository.findAllByChannelIdsWithUserAndChannel(privateChannelIds)
                        .stream()
                        .collect(
                                Collectors.groupingBy(readStatus ->
                                                readStatus.getChannel().getId(),
                                        Collectors.mapping(readStatus ->
                                                        userMapper.toDto(readStatus.getUser()),
                                                Collectors.toList()
                                        )
                                )
                        );

        List<ChannelDto> channelDtoList = channels.stream()
                .map(channel ->
                        channelMapper.toListDto(
                                channel,
                                participantMap,
                                lastMessageAtMap
                        )
                )
                .toList();

        log.debug("[CHANNEL_LIST_FIND] 채널 조회 목록 완료: count={}", channelDtoList.size());

        return channelDtoList;
    }

    @CacheEvict(value = "channelList", allEntries = true)
    @PreAuthorize("hasRole('CHANNEL_MANAGER')")
    @Override
    public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
        log.debug("[CHANNEL_UPDATE] 채널 정보 수정 시작: channelId={}, newName={}, newDescription={}",
                channelId, request.newName(), request.newDescription());

        // channel 객체 존재 확인
        Channel channel = validateAndGetChannelByChannelId(channelId);

        // PRIVATE Channel일 경우 수정 불가
        if (ChannelType.PRIVATE.equals(channel.getType())) {
            throw new PrivateChannelCannotBeUpdatedException(channelId);
        }

        // 입력값과 현재 값을 비교해서 같으면 null, 새롭게 입력된 값이면 입력값
        String newName = changedString(request.newName(), channel.getName());
        String newDescription = changedString(request.newDescription(), channel.getDescription());

        log.debug("[CHANNEL_UPDATE] 채널 수정 입력값 변경 여부: isChangedName={}, isChangedDescription={}",
                newName != null, newDescription != null);

        // 전부 입력 X이거나 전부 현재 값과 동일(전부 null)할 때 검증
        validateAllRequestExistingOrNull(newName, newDescription);

        channel.update(newName, newDescription);

        log.info("[CHANNEL_UPDATE] 채널 정보 수정 완료: channelId={}, type={}, name={}, description={}",
                channel.getId(), channel.getType(), channel.getName(), channel.getDescription());

        ChannelDto channelDto = channelMapper.toDto(channel);

        changeEventPublish(channel.getType(), ChangeType.UPDATED, channelDto);

        return channelDto;
    }

    @CacheEvict(value = "channelList", allEntries = true)
    @PreAuthorize("@channelAuthorizationEvaluator.canDelete(#channelId, authentication)")
    @Override
    public void delete(UUID channelId) {
        log.debug("[CHANNEL_DELETE] 채널 삭제 시작: channelId={}", channelId);

        // Channel ID null & channel 객체 존재 확인
        Channel channel = validateAndGetChannelByChannelId(channelId);
        ChannelDto channelDto = channelMapper.toDto(channel);

        // 채널 삭제 전 ReadStatus 삭제하여 참조 상태 해제(private channel)
        readStatusRepository.deleteByChannelId(channelId);
        // 채널 삭제
        channelRepository.delete(channel);

        changeEventPublish(channel.getType(), ChangeType.DELETED, channelDto);

        log.info("[CHANNEL_DELETE] 채널 삭제 완료: channelId={}", channelId);
    }

    // validation
    //로그인 되어있는 user ID null & user 객체 존재 확인
    private User validateAndGetUserByUserId(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException("userId", null);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }

    private Channel validateAndGetChannelByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new InvalidInputException("channelId", null);
        }
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    // 입력값과 현재 값을 비교해서 같으면 null, 새롭게 입력된 값이면 입력값
    private String changedString(String requestValue, String userValue) {
        return requestValue != null && !requestValue.equals(userValue)
                ? requestValue
                : null;
    }

    // type, name, channelDescription이 전부 입력되지 않았거나, 전부 이전과 동일하다면 exception
    private void validateAllRequestExistingOrNull(String newName, String newDescription) {
        if (newName == null && newDescription == null) {
            throw new NoChangeValueException("All UpdateRequestField", null);
        }
    }

    private void changeEventPublish(
            ChannelType channelType,
            ChangeType changeType,
            ChannelDto channelDto
    ) {
        if (ChannelType.PRIVATE.equals(channelType)) {
            applicationEventPublisher.publishEvent(
                    new PrivateChannelChangeEvent(
                            changeType,
                            channelDto
                    )
            );

            return;
        }

        applicationEventPublisher.publishEvent(
                new ChannelChangeEvent(
                        changeType,
                        channelDto
                )
        );
    }
}
