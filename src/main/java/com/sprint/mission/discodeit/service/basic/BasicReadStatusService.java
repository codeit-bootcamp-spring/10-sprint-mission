package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.DuplicationReadStatusException;
import com.sprint.mission.discodeit.exception.StatusNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public UUID create(ReadStatusCreateRequest request) {
        requireNonNull(request, "request");
        requireNonNull(request.userId(), "userId");
        requireNonNull(request.channelId(), "channelId");

        // 존재 확인
        userRepository.findById(request.userId())
                .orElseThrow(UserNotFoundException::new);

        // 채널 확인
        if (channelRepository.findChannel(request.channelId()) == null) {
            throw new ChannelNotFoundException();
        }

        ReadStatus duplicated =
                readStatusRepository.findByUserIdAndChannelId(
                        request.userId(), request.channelId());

        if (duplicated != null) {
            throw new DuplicationReadStatusException();
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatus.updateLastReadAt(Instant.now());

        return readStatusRepository.save(readStatus).getId();
    }

    @Override
    public ReadStatus find(UUID id) {
        requireNonNull(id, "id");

        ReadStatus readStatus = readStatusRepository.findById(id);

        if (readStatus == null) {
            throw new StatusNotFoundException();
        }
        return readStatus;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        requireNonNull(userId, "userId");

        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(ReadStatusUpdateRequest request) {
        requireNonNull(request, "request");
        requireNonNull(request.readStatus(), "readStatus");

        ReadStatus readStatus = readStatusRepository.findById(request.readStatus());
        if (readStatus == null) {
            throw new StatusNotFoundException();
        }
        readStatus.updateLastReadAt(Instant.now());
        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        requireNonNull(id, "id");
        readStatusRepository.delete(id);
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}