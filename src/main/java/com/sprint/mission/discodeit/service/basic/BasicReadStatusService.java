package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        // 관련된 Channel, User 있는지 확인
        if (!userRepository.existsById(request.userId())) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다. id: " + request.userId());
        }
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("존재하지 않는 채널입니다. id: " + request.channelId());
        }

        // 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외 발생
        boolean exists = readStatusRepository.findAll().stream()
                .anyMatch(rs -> rs.getUserId().equals(request.userId())
                        && rs.getChannelId().equals(request.channelId()));
        if (exists) {
            throw new IllegalArgumentException("해당 유저의 readStatus가 이미 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(request.channelId(), request.userId());
        ReadStatus saved = readStatusRepository.save(readStatus);

        return toResponse(saved);
    }

    @Override
    public ReadStatusResponse find(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("읽기 상태(readStatus)를 찾을 수 없습니다. id: " + readStatusId));
        return toResponse(readStatus);
    }

    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAll().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("수정할 읽기 상태(readStatus)를 찾을 수 없습니다. id: " + request.id()));

        // 읽은 시간 갱신
        readStatus.markAsRead();
        ReadStatus updated = readStatusRepository.save(readStatus);

        return toResponse(updated);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existById(readStatusId)) {
            throw new NoSuchElementException("삭제할 읽기 상태가 존재하지 않습니다. id: " + readStatusId);
        }
        readStatusRepository.deleteById(readStatusId);
    }

    private ReadStatusResponse toResponse(ReadStatus readStatus) {
        return new ReadStatusResponse(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getUpdatedAt() // 마지막으로 읽은 시간
        );
    }
}
