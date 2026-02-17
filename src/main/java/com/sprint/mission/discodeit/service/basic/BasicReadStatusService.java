package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusDto create(UUID channelId, UUID userId) {
        validateUserAndChannel(userId, channelId);

        if (readStatusRepository.findByChannelIdAndUserId(channelId, userId).isPresent()) {
            throw new BusinessException(ErrorCode.READ_STATUS_ALREADY_EXISTS);
        }

        ReadStatus readStatus = new ReadStatus(userId, channelId, Instant.now());
        readStatusRepository.save(readStatus);

        return toDto(readStatus);
    }

    @Override
    public ReadStatusDto find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new BusinessException(ErrorCode.READ_STATUS_NOT_FOUND));
        return toDto(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ReadStatusDto update(UUID channelId, UUID userId) {
        validateUserAndChannel(userId, channelId);

        ReadStatus readStatus = readStatusRepository.findByChannelIdAndUserId(channelId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.READ_STATUS_NOT_FOUND));

        readStatus.update(Instant.now());
        readStatusRepository.save(readStatus);

        return toDto(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new BusinessException(ErrorCode.READ_STATUS_NOT_FOUND);
        }

        readStatusRepository.deleteById(readStatusId);
    }

    private ReadStatusDto toDto(ReadStatus userStatus) {
        return new ReadStatusDto(
                userStatus.getId(),
                userStatus.getCreatedAt(),
                userStatus.getUpdatedAt(),
                userStatus.getUserId(),
                userStatus.getChannelId(),
                userStatus.getLastReadAt()
        );
    }

    private void validateUserAndChannel(UUID userId, UUID channelId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!channelRepository.existsById(channelId)) {
            throw new BusinessException(ErrorCode.CHANNEL_NOT_FOUND);
        }
    }
}
