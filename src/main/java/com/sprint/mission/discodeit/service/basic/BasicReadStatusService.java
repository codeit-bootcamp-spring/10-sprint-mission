package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.code.ErrorCode;
import com.sprint.mission.discodeit.common.exception.custom.APIException;
import com.sprint.mission.discodeit.common.util.TimeConverter;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusServiceDTO.ReadStatusUpdateCommand;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService extends BasicDomainService<ReadStatus> implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.filter(status -> status.matchUserId(userId))
                .map(ReadStatus::toResponse)
                .toList();
    }

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        verifyCreatable(request);

        ReadStatus status = new ReadStatus(request.userId(), request.channelId(),
                TimeConverter.toInstant(request.lastReadAt()));
        readStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public ReadStatusResponse find(UUID id) {
        return findById(id).toResponse();
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateCommand command) {
        ReadStatus status = findById(command.id());
        status.update(command.datetime());
        readStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public void delete(UUID id) {
        deleteIfExist(id, readStatusRepository, () -> new APIException(ErrorCode.READSTATUSID_NOT_FOUND, id));
    }

    @Override
    protected ReadStatus findById(UUID id) {
        return findEntityById(id, readStatusRepository, () -> new APIException(ErrorCode.READSTATUSID_NOT_FOUND, id));
    }

    @Override
    public ReadStatusResponse find(UUID channelId, UUID userId) {
        return findByChannelAndUser(channelId, userId).toResponse();
    }

    private ReadStatus findByChannelAndUser(UUID channelId, UUID userId) {
        return readStatusRepository.filter(readStatus -> readStatus.matchChannelId(channelId))
                .filter(readStatus -> readStatus.matchUserId(userId))
                .findFirst()
                .orElseThrow(() -> new APIException(ErrorCode.READSTATUSID_NOT_FOUND, Map.of("userId", userId, "channelId", channelId)));
    }

    private void verifyCreatable(ReadStatusCreateRequest request) {
        ensure(() -> userRepository.existsById(request.userId()),
                () -> new APIException(ErrorCode.USERID_NOT_FOUND, request.userId()));
        ensure(() -> channelRepository.existsById(request.channelId()),
                () -> new APIException(ErrorCode.CHANNELID_NOT_FOUND, request.channelId()));
        ensure(() -> readStatusRepository.existsByUserAndChannelId(request.userId(), request.channelId()),
                () -> new APIException(ErrorCode.READSTATUS_ALREADY_EXIST, Map.of("userId", request.userId(), "channelId", request.channelId())));
    }

}
