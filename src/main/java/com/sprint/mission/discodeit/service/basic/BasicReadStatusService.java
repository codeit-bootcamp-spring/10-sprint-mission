package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService extends BasicDomainService<ReadStatus> implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) throws IOException {
        return readStatusRepository.filter(status -> status.matchUserId(userId))
                .map(ReadStatus::toResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest model) throws IOException {
        // todo: refactoring
        if (!userRepository.existsById(model.userId())) {
            throw new NoSuchElementException(ID_NOT_FOUND.formatted("User", model.userId()));
        }
        if (!channelRepository.existsById(model.channelId())) {
            throw new NoSuchElementException(ID_NOT_FOUND.formatted("Channel", model.channelId()));
        }
        if (readStatusRepository.existsByUserAndChannelId(model.userId(), model.channelId())) {
            throw new IllegalStateException(
                    "read status entity exist already containing (user id: %s, channel id: %s)".formatted(model.userId(), model.channelId()));
        }
        ReadStatus status = new ReadStatus(model.userId(), model.channelId());
        readStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public ReadStatusResponse find(UUID id) throws IOException, ClassNotFoundException {
        return findById(id).toResponse();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest model) throws IOException, ClassNotFoundException {
        ReadStatus status = findById(model.id());
        status.update(model.type());
        readStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public void delete(UUID id) throws IOException {
        if (readStatusRepository.existsById(id)) {
            readStatusRepository.deleteById(id);
            return;
        }
        throw new NoSuchElementException(ID_NOT_FOUND.formatted("ReadStatus", id));
    }

    @Override
    protected ReadStatus findById(UUID id) throws IOException, ClassNotFoundException {
        return findEntityById(id, "ReadStatus", readStatusRepository);
    }
}
