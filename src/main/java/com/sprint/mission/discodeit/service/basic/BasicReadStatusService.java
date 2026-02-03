package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    public ReadStatusResponse createStatus(ReadStatusCreateRequest request) {
        if (!userRepository.findById(request.getUserId()).isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        if (!channelRepository.findById(request.getChannelId()).isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        return readStatusRepository.findByUserIdAndChannelId(request.getUserId(), request.getChannelId())
                .map(readStatusMapper::toResponse)
                .orElseGet(() -> {
                    ReadStatus readStatus = new ReadStatus(request.getUserId(), request.getChannelId());
                    readStatusRepository.save(readStatus);
                    return readStatusMapper.toResponse(readStatus);
                });
    }

    public ReadStatusResponse findStatus(UUID id) {
        ReadStatus readStatus = readStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 읽기 상태입니다."));
        return readStatusMapper.toResponse(readStatus);
    }

    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ReadStatusResponse updateStatus(ReadStatusUpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 읽기 상태입니다."));
        readStatus.updateLastReadAt();
        readStatusRepository.save(readStatus);
        return readStatusMapper.toResponse(readStatus);
    }

    public void deleteStatus(UUID id) {
        readStatusRepository.delete(id);
    }
}
