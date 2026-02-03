package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusCreation;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusUpdate;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.ReadType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public ReadStatusResponse create(ReadStatusCreation model) {
        if (!userRepository.existsById(model.userId())) {
            throw new NoSuchElementException("User with id, %s, not found".formatted(model.userId()));
        }
        if (!channelRepository.existsById(model.channelId())) {
            throw new NoSuchElementException("Channel with id, %s, not found".formatted(model.channelId()));
        }
        if (hasReadStatus(model.userId(), model.channelId())) {
            throw new IllegalStateException(
                    "read status entity exist already containing (user id: %s, channel id: %s)".formatted(model.userId(), model.channelId()));
        }
        ReadStatus status = new ReadStatus(model.userId(), model.channelId());
        readStatusRepository.save(status);
        return status.toResponse();
    }

    private boolean hasReadStatus(UUID userId, UUID channelId) {
        return readStatusRepository.findAll()
                .stream()
                .anyMatch(status -> status.matchUserId(userId) && status.matchChannelId(channelId));
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        return null;
    }

    @Override
    public List<ReadStatusResponse> findAll() {
        return List.of();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdate model) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
