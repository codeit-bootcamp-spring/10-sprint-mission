package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.response.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatus createReadStatus(ReadStatusCreateRequest request) {
        validateUserExists(request.userId());
        validateChannelExists(request.channelId());
        validateDuplicateReadStatus(request);

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId(),
                request.lastReadAt()
        );

        readStatusRepository.save(readStatus);
        return readStatus;
    }

    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND,
                    "존재하지 않는 userId 입니다. userId: " + userId);
        }
    }

    private void validateChannelExists(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ApiException(ErrorCode.CHANNEL_NOT_FOUND,
                    "존재하지 않는 channelId 입니다. channelId: " + channelId);
        }
    }

    private void validateDuplicateReadStatus(ReadStatusCreateRequest request) {
        if (readStatusRepository.existsByUserIdAndChannelId(request.userId(),
                request.channelId())) {
            throw new ApiException(ErrorCode.READ_STATUS_ALREADY_EXISTS,
                    "이미 존재하는 readStatus 입니다 userId: " + request.userId()
                            + ", channelId: " + request.channelId());
        }
    }

    @Override
    public ReadStatus findReadStatusByReadStatusId(UUID readStatusId) {
        return getReadStatusOrThrow(readStatusId);
    }

    @Override
    public List<ReadStatus> findAllReadStatusesByUserId(UUID userId) {
        validateUserExists(userId);

        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus updateReadStatus(UUID readStatusId, ReadStatusUpdateRequest request) {
        ReadStatus readStatus = getReadStatusOrThrow(readStatusId);

        readStatus.updateLastReadAt(request.newLastReadAt());
        readStatusRepository.save(readStatus);
        return readStatus;
    }

    @Override
    public void deleteReadStatus(UUID readStatusId) {
        readStatusRepository.deleteById(readStatusId);
    }

    private ReadStatus getReadStatusOrThrow(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new ApiException(ErrorCode.READ_STATUS_NOT_FOUND,
                        "ReadStatus 를 찾을 수 없습니다 readStatusId: " + readStatusId));
    }
}
