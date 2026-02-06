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
    private final String ID_NOT_FOUND = "ReadStatus with id, %s, not found";
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findByUserId(userId)
                .stream()
                .map(ReadStatus::toResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse create(ReadStatusCreation model) {
        // todo: refactoring
        if (!userRepository.existsById(model.userId())) {
            throw new NoSuchElementException("User with id, %s, not found".formatted(model.userId()));
        }
        if (!channelRepository.existsById(model.channelId())) {
            throw new NoSuchElementException("Channel with id, %s, not found".formatted(model.channelId()));
        }
        if (readStatusRepository.existsByUserAndChannelId(model.userId(), model.channelId())) {
            throw new IllegalStateException(
                    "read status entity exist already containing (user id: %s, channel id: %s)".formatted(model.userId(), model.channelId()));
        }
        ReadStatus status = new ReadStatus(model.userId(), model.channelId());
        readStatusRepository.save(status);
        return status.toResponse();
    }

    private ReadStatus findReadStatus(UUID id) {
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ID_NOT_FOUND.formatted(id)));
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        return findReadStatus(id).toResponse();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdate model) {
        ReadStatus status = findReadStatus(model.id());
        status.update(model.type());
        readStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public void delete(UUID id) {
        if (readStatusRepository.existsById(id)) {
            readStatusRepository.deleteById(id);
            return;
        }
        throw new NoSuchElementException(ID_NOT_FOUND.formatted(id));
    }
}
