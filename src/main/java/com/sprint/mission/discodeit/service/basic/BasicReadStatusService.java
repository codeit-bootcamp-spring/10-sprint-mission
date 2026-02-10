package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusRequestUpdateDto;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponseDto create(ReadStatusRequestCreateDto request) {
        Validators.validateCreateReadStatusRequest(request);
        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("유저 Id가 존재하지 않습니다."));

        boolean exists = readStatusRepository.findAll().stream()
                .anyMatch(rs -> rs.getUserId().equals(request.userId())
                        && rs.getChannelId().equals(request.channelId()));

        if (exists) {
            throw new IllegalArgumentException("ReadStatus가 이미 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        return toDto(readStatusRepository.save(readStatus));
    }

    @Override
    public ReadStatusResponseDto find(UUID id) {
        ReadStatus readStatus = validateExistenceReadStatus(id);
        return toDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID id) {
       return readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(id))
                .map(BasicReadStatusService::toDto)
                .toList();
    }

    @Override
    public void update(ReadStatusRequestUpdateDto request) {
        Validators.requireNonNull(request, "request");
        ReadStatus readStatus = validateExistenceReadStatus(request.id());
        readStatus.updateLastReadAt();
        readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID id) {
        validateExistenceReadStatus(id);
        readStatusRepository.deleteById(id);
    }

    private ReadStatus validateExistenceReadStatus(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus가 존재하지 않습니다."));

    }


    public static ReadStatusResponseDto toDto(ReadStatus readStatus) {
        return new ReadStatusResponseDto(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadAt()
        );
    }
}
