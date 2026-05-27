package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
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

    @Override
    public ReadStatusDto create(ReadStatusCreateRequest request) {
        log.debug("[READ_STATUS_CREATE] 마지막 메시지 읽음 상태 생성 시작: userId={}, channelId={}, lastReadAt={}", request.userId(), request.channelId(), request.lastReadAt());

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

        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);

        readStatusRepository.save(readStatus);
        log.info("[READ_STATUS_CREATE] 마지막 메시지 읽음 상태 생성 완료: readStatusId={}, userId={}, channelId={}, lastReadAt={}", readStatus.getId(), readStatus.getUser().getId(), readStatus.getChannel().getId(), readStatus.getLastReadAt());

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadStatusDto find(UUID readStatusId) {
        log.debug("[READ_STATUS_FIND] 마지막 메시지 읽음 상태 조회 시작: readStatusId={}", readStatusId);

        ReadStatus readStatus = validateAndGetReadStatusByReadStatusId(readStatusId);
        log.debug("[READ_STATUS_FIND] 마지막 메시지 읽음 상태 조회 완료: readStatusId={}, userId={}, channelId={}, lastReadAt={}", readStatus.getId(), readStatus.getUser().getId(), readStatus.getChannel().getId(), readStatus.getLastReadAt());

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.debug("[READ_STATUS_LIST_FIND_BY_USERID] 마지막 메시지 읽음 상태 목록 조회 시작: userId={}", userId);

        // user ID null & user 객체 존재 확인
        validateAndGetUserByUserId(userId);
        List<ReadStatusDto> readStatusDtoList = readStatusRepository.findAllByUserIdWithUserAndChannel(userId).stream()
                .map(readStatus -> readStatusMapper.toDto(readStatus))
                .toList();
        log.debug("[READ_STATUS_LIST_FIND_BY_USERID] 마지막 메시지 읽음 상태 목록 조회 완료: count={}", readStatusDtoList.size());

        return readStatusDtoList;
    }

    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
        log.debug("[READ_STATUS_UPDATE] 마지막 메시지 읽음 상태 수정 시작: readStatusId={}, newLastReadAt={}", readStatusId, request.newLastReadAt());

        if (request.newLastReadAt() == null) {
            throw new InvalidInputException("newLastReadAt", null);
        }
        ReadStatus readStatus = validateAndGetReadStatusByReadStatusId(readStatusId);

        readStatus.updateLastReadTime(request.newLastReadAt());
        log.info("[READ_STATUS_UPDATE] 마지막 메시지 읽음 상태 수정 완료: readStatusId={}, userId={}, channelId={}, lastReadAt={}", readStatus.getId(), readStatus.getUser().getId(), readStatus.getChannel().getId(), readStatus.getLastReadAt());

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        log.debug("[READ_STATUS_DELETE] 마지막 메시지 읽음 상태 삭제 시작: readStatusId={}", readStatusId);

        validateReadStatusByReadStatusId(readStatusId);
        readStatusRepository.deleteById(readStatusId);
        log.info("[READ_STATUS_DELETE] 마지막 메시지 읽음 상태 삭제 완료: readStatusId={}", readStatusId);
    }

    //// validation
    // user ID null & user 객체 존재 확인
    private User validateAndGetUserByUserId(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException("userId", userId);
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }

    private Channel validateAndGetChannelByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new InvalidInputException("channelId", channelId);
        }
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    private ReadStatus validateAndGetReadStatusByReadStatusId(UUID readStatusId) {
        if (readStatusId == null) {
            throw new InvalidInputException("readStatusId", readStatusId);
        }
        return readStatusRepository.findByIdWithUserAndChannel(readStatusId)
                .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
    }

    private void validateReadStatusByReadStatusId(UUID readStatusId) {
        if (readStatusId == null) {
            throw new InvalidInputException("readStatusId", readStatusId);
        }
        readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
    }
}
