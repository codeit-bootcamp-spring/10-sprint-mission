package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.common.NoChangeValueException;
import com.sprint.mission.discodeit.exception.readstatus.DuplicatedReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @CacheEvict(value = "channelList", allEntries = true)
    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        log.debug("[READ_STATUS_CREATE] ReadStatus 생성 시작: userId={}, channelId={}, lastReadAt={}",
                request.userId(), request.channelId(), request.lastReadAt());

        UUID userId = request.userId();
        UUID channelId = request.channelId();
        Instant lastReadAt = request.lastReadAt();

        // user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);
        // channel 객체 존재 확인
        Channel channel = validateAndGetChannelByChannelId(channelId);

        if (readStatusRepository.existsReadStatusByUserIdAndChannelId(userId, channelId)) {
            throw new DuplicatedReadStatusException(userId, channelId);
        }

        ReadStatus readStatus = channel.getType().equals(ChannelType.PUBLIC)
                // Public 채널 알림 여부 false로 초기화
                ? new ReadStatus(user, channel, lastReadAt, false)
                // Private 채널 알림 여부 true로 초기화
                : new ReadStatus(user, channel, lastReadAt, true);

        readStatusRepository.save(readStatus);

        log.info("[READ_STATUS_CREATE] ReadStatus 생성 완료: readStatusId={}, userId={}, channelId={}, lastReadAt={}, notificationEnabled={}",
                readStatus.getId(), readStatus.getUser().getId(), readStatus.getChannel().getId(), readStatus.getLastReadAt(), readStatus.isNotificationEnabled());

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID readStatusId) {
        log.debug("[READ_STATUS_FIND] ReadStatus 조회 시작: readStatusId={}", readStatusId);

        ReadStatus readStatus = validateAndGetReadStatusByReadStatusId(readStatusId);

        log.debug("[READ_STATUS_FIND] ReadStatus 조회 완료: readStatusId={}, userId={}, channelId={}, lastReadAt={}, notificationEnabled={}",
                readStatus.getId(), readStatus.getUser().getId(), readStatus.getChannel().getId(), readStatus.getLastReadAt(), readStatus.isNotificationEnabled());

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.debug("[READ_STATUS_LIST_FIND_BY_USERID] ReadStatus 목록 조회 시작: userId={}", userId);

        // user ID null & user 객체 존재 확인
        validateAndGetUserByUserId(userId);
        List<ReadStatusDto> readStatusDtoList = readStatusRepository.findAllByUserIdWithUserAndChannel(userId).stream()
                .map(readStatus -> readStatusMapper.toDto(readStatus))
                .toList();

        log.debug("[READ_STATUS_LIST_FIND_BY_USERID] ReadStatus 목록 조회 완료: count={}", readStatusDtoList.size());

        return readStatusDtoList;
    }

    @PreAuthorize("@readStatusAuthorizationEvaluator.isOwner(#readStatusId, authentication.principal)")
    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        log.debug("[READ_STATUS_UPDATE] ReadStatus 수정 시작: readStatusId={}, newLastReadAt={}, newNotificationEnabled={}",
                readStatusId, request.newLastReadAt(), request.newNotificationEnabled());

        ReadStatus readStatus = validateAndGetReadStatusByReadStatusId(readStatusId);

        Instant newLastReadAt = changedInstant(
                request.newLastReadAt(),
                readStatus.getLastReadAt()
        );
        Boolean newNotificationEnabled = changedBoolean(
                request.newNotificationEnabled(),
                readStatus.isNotificationEnabled()
        );

        // 전부 입력 X이거나 전부 현재값과 동일(전부 null)할 때 검증
        validateAllRequestExistingOrNull(newLastReadAt, newNotificationEnabled);

        readStatus.update(newLastReadAt, newNotificationEnabled);

        log.info("[READ_STATUS_UPDATE] ReadStatus 수정 완료: readStatusId={}, userId={}, channelId={}, lastReadAt={}, notificationEnabled={}",
                readStatus.getId(), readStatus.getUser().getId(), readStatus.getChannel().getId(), readStatus.getLastReadAt(), readStatus.isNotificationEnabled());

        return readStatusMapper.toDto(readStatus);
    }

    @CacheEvict(value = "channelList", allEntries = true)
    @PreAuthorize("@readStatusAuthorizationEvaluator.isOwner(#readStatusId, authentication.principal)")
    @Override
    public void delete(UUID readStatusId) {
        log.debug("[READ_STATUS_DELETE] ReadStatus 삭제 시작: readStatusId={}", readStatusId);

        validateReadStatusByReadStatusId(readStatusId);
        readStatusRepository.deleteById(readStatusId);

        log.info("[READ_STATUS_DELETE] ReadStatus 삭제 완료: readStatusId={}", readStatusId);
    }

    //// validation
    // user ID null & user 객체 존재 확인
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

    private ReadStatus validateAndGetReadStatusByReadStatusId(UUID readStatusId) {
        if (readStatusId == null) {
            throw new InvalidInputException("readStatusId", null);
        }
        return readStatusRepository.findByIdWithUserAndChannel(readStatusId)
                .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
    }

    private Instant changedInstant(Instant requestValue, Instant readStatusValue) {
        return requestValue != null && !requestValue.equals(readStatusValue)
                ? requestValue
                : null;
    }

    private Boolean changedBoolean(Boolean requestValue, Boolean readStatusValue) {
        return requestValue != null && !requestValue.equals(readStatusValue)
                ? requestValue
                : null;
    }

    private void validateAllRequestExistingOrNull(
            Instant lastReadAt,
            Boolean notificationEnabled
    ) {
        if (lastReadAt == null && notificationEnabled == null) {
            throw new NoChangeValueException("All UpdateRequestField", null);
        }
    }


    private void validateReadStatusByReadStatusId(UUID readStatusId) {
        if (readStatusId == null) {
            throw new InvalidInputException("readStatusId", null);
        }
        readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
    }
}
