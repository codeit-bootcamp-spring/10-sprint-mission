package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.ChannelEvents;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 채널별 읽기 상태(읽음 확인, 알림 설정 등)를 관리하는 기본 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 새로운 읽기 상태를 생성합니다.
     * 이미 존재하는 경우 기존 데이터를 반환하여 멱등성을 보장합니다.
     *
     * @param request 읽기 상태 생성 요청 정보
     * @return 생성 또는 조회된 읽기 상태 정보
     */
    @Override
    @Transactional
    public ReadStatusDto.Response create(ReadStatusDto.CreateRequest request) {
        UUID userId = request.userId();
        UUID channelId = request.channelId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException.withId(channelId));

        return readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                .map(readStatusMapper::toResponse)
                .orElseGet(() -> {
                    ReadStatusDto.Response response = saveNewReadStatus(user, channel, request.lastReadAt());
                    // 접근 권한(채널 목록) 변경 알림
                    eventPublisher.publishEvent(new ChannelEvents.AccessChanged(userId));
                    return response;
                });
        }

    /**
     * 읽기 상태 정보를 ID로 조회합니다.
     */
    @Override
    public ReadStatusDto.Response find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .map(readStatusMapper::toResponse)
                .orElseThrow(() -> ReadStatusNotFoundException.withId(readStatusId));
    }

    /**
     * 특정 사용자의 모든 읽기 상태 목록을 조회합니다.
     */
    @Override
    public List<ReadStatusDto.Response> findAllByUserId(UUID userId) {
        validateUserExists(userId);
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toResponse)
                .toList();
    }

    /**
     * 읽기 상태 정보(마지막 읽은 시간, 알림 여부)를 업데이트합니다.
     */
    @Override
    @Transactional
    public ReadStatusDto.Response update(UUID readStatusId, ReadStatusDto.UpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> ReadStatusNotFoundException.withId(readStatusId));

        readStatus.update(request.newLastReadAt(), request.newNotificationEnabled());
        log.debug("[ReadStatus] 상태 업데이트: ID={}, UserId={}, ChannelId={}", 
                readStatusId, readStatus.getUser().getId(), readStatus.getChannel().getId());

        // 캐시 무효화 (알림 설정/읽기 시간 변경 반영)
        eventPublisher.publishEvent(new ChannelEvents.AccessChanged(readStatus.getUser().getId()));

        return readStatusMapper.toResponse(readStatus);
    }

    /**
     * 읽기 상태 정보를 삭제합니다.
     */
    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> ReadStatusNotFoundException.withId(readStatusId));

        UUID userId = readStatus.getUser().getId();
        readStatusRepository.delete(readStatus);

        log.info("[ReadStatus] 상태 삭제: ID={}", readStatusId);

        // 접근 권한(채널 목록) 변경 알림
        eventPublisher.publishEvent(new ChannelEvents.AccessChanged(userId));
    }

    // --- Private Helpers ---

    private ReadStatusDto.Response saveNewReadStatus(User user, Channel channel, Instant lastReadAt) {
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        try {
            ReadStatus savedStatus = readStatusRepository.saveAndFlush(readStatus);
            log.info("[ReadStatus] 신규 상태 생성: UserId={}, ChannelId={}", user.getId(), channel.getId());
            return readStatusMapper.toResponse(savedStatus);
        } catch (DataIntegrityViolationException e) {
            return readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
                    .map(readStatusMapper::toResponse)
                    .orElseThrow(() -> InternalServerException.dataIntegrity("ReadStatus 생성 실패 및 조회 불가: User %s, Channel %s", user.getId(), channel.getId()));
        }
    }

    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }
    }
}
