package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.global.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.status.read.ReadStatusNotFoundException;
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

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusDto createReadStatus(CreateReadStatusRequestDTO dto) {
        if (dto.userId() == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("userId", "userId is null")
            );
        }
        if (dto.channelId() == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("channelId", "channelId is null")
            );
        }

        // 객체 검증
        User user = findUserOrThrow(dto.userId());
        Channel channel = findChannelOrThrow(dto.channelId());

        // 중복 검증
        checkStatusAlreadyExists(dto.userId(), dto.channelId());

        ReadStatus status = new ReadStatus(user, channel);
        readStatusRepository.save(status);

        log.info("[READSTATUS_CREATE_SUCCESS] 읽음 상태 생성 성공: readStatusId={}", status.getId());
        return readStatusMapper.toDto(status);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusDto findById(UUID statusId) {
        ReadStatus status = findReadStatusOrThrow(statusId);

        return readStatusMapper.toDto(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        findUserOrThrow(userId);
        List<ReadStatus> statuses = readStatusRepository.findAllByUser_Id(userId);

        return readStatusMapper.toDtoList(statuses);
    }

    @Override
    public ReadStatusDto updateReadStatus(UUID statusId, UpdateReadStatusRequestDTO dto) {
        if (dto == null) {
            throw new InvalidInputException(
                    ErrorCode.DTO_CAN_NOT_BE_NULL, Map.of("dto", "dto is null")
            );
        }

        if (dto.newLastReadAt() == null) {
            log.warn("[READSTATUS_UPDATE_FAIL_BY_LAST_READ_AT] lastReadAt이 null값으로 읽음 상태 수정 실패: readStatusId={}", statusId);
            throw new InvalidInputException(
                    ErrorCode.LAST_READ_AT_IS_NULL, Map.of("readStatusId", statusId)
            );
        }

        ReadStatus status = findReadStatusOrThrow(statusId);
        status.updateLastReadAt(dto.newLastReadAt());

        log.info("[READSTATUS_UPDATE_SUCCESS] 읽음 상태 수정 성공: readStatusId={}", statusId);
        return readStatusMapper.toDto(status);
    }

    @Override
    public void deleteById(UUID statusId) {
        findReadStatusOrThrow(statusId);

        log.info("[READSTATUS_DELETE_SUCCESS] 읽음 상태 삭제 성공: readStatusId={}", statusId);
        readStatusRepository.deleteById(statusId);
    }

    private ReadStatus findReadStatusOrThrow(UUID statusId) {
        if (statusId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("statusId", "statusId is null")
            );
        }

        return readStatusRepository.findById(statusId)
                .orElseThrow(() -> {
                    log.warn("[READSTATUS_NOT_FOUND] 읽음 상태가 존재하지 않음: readStatusId={}", statusId);
                    return new ReadStatusNotFoundException(statusId);
                });
    }

    private User findUserOrThrow(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("userId", "userId is null")
            );
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 유저가 존재하지 않음: userId={}", userId);
                    return new UserNotFoundException(userId);
                });
    }

    private Channel findChannelOrThrow(UUID channelId) {
        if (channelId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("channelId", "channelId is null")
            );
        }

        return channelRepository.findById(channelId)
                .orElseThrow(() -> {
                    log.warn("[CHANNEL_NOT_FOUND] 채널이 존재하지 않음: channelId={}", channelId);
                    return new ChannelNotFoundException(channelId);
                });
    }

    private void checkStatusAlreadyExists(UUID userId, UUID channelId) {
        if (readStatusRepository.existsByUser_IdAndChannel_Id(userId, channelId)) {
            log.warn("[READSTATUS_ALREADY_EXISTS] 읽음 상태가 이미 존재함: userId={}, channelId={}", userId, channelId);
            throw new DuplicateResourceException(
                    ErrorCode.READ_STATUS_ALREADY_EXISTS, Map.of("userId", userId, "channelId", channelId)
            );
        }
    }
}
