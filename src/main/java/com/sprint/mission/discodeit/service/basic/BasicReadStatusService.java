package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
    public UUID createReadStatus(CreateReadStatusRequest request) {
        validateUserExists(request.userId());
        validateChannelExists(request.channelId());
        validateDuplicateReadStatus(request);

        ReadStatus readStatus = new ReadStatus(
                request.userId(),
                request.channelId()
        );

        readStatusRepository.save(readStatus);
        return readStatus.getId();
    }

    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 userId 입니다. userId: " + userId);
        }
    }

    private void validateChannelExists(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("존재하지 않는 channelId 입니다. channelId: " + channelId);
        }
    }

    private void validateDuplicateReadStatus(CreateReadStatusRequest request) {
        if (readStatusRepository.existsByChannelId(request.channelId())) {
            throw new IllegalArgumentException("이미 존재하는 readStatus 입니다 channelId: " + request.channelId());
        }

        if (readStatusRepository.existsByUserId(request.userId())) {
            throw new IllegalArgumentException("이미 존재하는 readStatus 입니다 userId: " + request.userId());
        }
    }

    @Override
    public ReadStatusResponse findReadStatusByReadStatusId(UUID readStatusId) {
        ReadStatus readStatus = getReadStatusOrThrow(readStatusId);

        return ReadStatusResponse.from(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllReadStatusesByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

        return readStatuses.stream()
                .map(readStatus -> ReadStatusResponse.from(readStatus)
                ).toList();
    }

    @Override
    public ReadStatusResponse updateReadStatus(UpdateReadStatusRequest request) {
        ReadStatus readStatus = getReadStatusOrThrow(request.id());

        readStatus.updateReadStatusType();
        readStatusRepository.save(readStatus);
        return ReadStatusResponse.from(readStatus);
    }

    @Override
    public void deleteReadStatus(UUID readStatusId) {
        readStatusRepository.deleteById(readStatusId);
    }

    private ReadStatus getReadStatusOrThrow(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus 를 찾을 수 없습니다 readStatusId: " + readStatusId));
    }
}
