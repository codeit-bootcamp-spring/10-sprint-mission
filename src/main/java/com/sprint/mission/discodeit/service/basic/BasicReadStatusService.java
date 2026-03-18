package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDto createReadStatus(ReadStatusCreateRequest request) {
        User user = getUserOrThrow(request.userId());
        Channel channel = getChannelOrThrow(request.channelId());
        validateDuplicateReadStatus(request);

        ReadStatus readStatus = new ReadStatus(
                user,
                channel,
                request.lastReadAt()
        );

        readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusDto findReadStatusByReadStatusId(UUID readStatusId) {
        return readStatusMapper.toDto(getReadStatusOrThrow(readStatusId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllReadStatusesByUserId(UUID userId) {
        User user = getUserOrThrow(userId);

        return readStatusRepository.findAllByUser(user).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusDto updateReadStatus(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = getReadStatusOrThrow(readStatusId);

        readStatus.updateLastReadAt(request.newLastReadAt());
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public void deleteReadStatus(UUID readStatusId) {
        ReadStatus readStatus = getReadStatusOrThrow(readStatusId);
        readStatus.assignUser(null);
        readStatus.assignChannel(null);
        readStatusRepository.delete(readStatus);
    }

    @Override
    @Transactional
    public void createInitialReadStatuses(
            UUID channelId,
            Set<UUID> participantIds,
            Instant lastReadAt
    ) {
        Channel channel = getChannelOrThrow(channelId);
        List<ReadStatus> readStatuses = new ArrayList<>();

        for (UUID participantId : participantIds) {
            User user = getUserOrThrow(participantId);
            if (readStatusRepository.existsByUserIdAndChannelId(participantId, channelId)) {
                continue;
            }
            readStatuses.add(new ReadStatus(user, channel, lastReadAt));
        }

        readStatusRepository.saveAll(readStatuses);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHANNEL_NOT_FOUND,
                        "채널을 찾을 수 없습니다 channelId: " + channelId));
    }

    private void validateDuplicateReadStatus(ReadStatusCreateRequest request) {
        if (readStatusRepository.existsByUserIdAndChannelId(request.userId(), request.channelId())) {
            throw new ApiException(ErrorCode.READ_STATUS_ALREADY_EXISTS,
                    "이미 존재하는 readStatus 입니다 userId: " + request.userId()
                            + ", channelId: " + request.channelId());
        }
    }

    private ReadStatus getReadStatusOrThrow(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new ApiException(ErrorCode.READ_STATUS_NOT_FOUND,
                        "ReadStatus 를 찾을 수 없습니다 readStatusId: " + readStatusId));
    }
}
