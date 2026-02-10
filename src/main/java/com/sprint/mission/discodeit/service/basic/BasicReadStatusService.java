package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
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

        if (userRepository.findById(request.userId()) == null) {
            throw new UserNotFoundException();
        }

        channelRepository.findChannel(request.channelId());

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
    public ReadStatusResponse find(UUID id) {
        requireNonNull(id, "id");

        ReadStatus readStatus = readStatusRepository.findById(id);
        if (readStatus == null) {
            throw new StatusNotFoundException();
        }

        return toDto(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        requireNonNull(userId, "userId");

        if (userRepository.findById(userId) == null) {
            throw new UserNotFoundException();
        }

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest req) {
        requireNonNull(req, "req");
        requireNonNull(req.readStatus(), "readStatus");

        ReadStatus rs = readStatusRepository.findById(req.readStatus());
        if (rs == null) {
            throw new StatusNotFoundException();
        }

        rs.updateLastReadAt(Instant.now());
        readStatusRepository.save(rs);

        return toDto(rs);
    }

    @Override
    public void delete(UUID id) {
        requireNonNull(id, "id");

        ReadStatus existing = readStatusRepository.findById(id);
        if (existing == null) {
            throw new StatusNotFoundException();
        }

        readStatusRepository.delete(id);
    }

    private ReadStatusResponse toDto(ReadStatus rs) {
        return new ReadStatusResponse(
                rs.getId(),
                rs.getUserId(),
                rs.getChannelId(),
                rs.getReadAt()
        );
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}
