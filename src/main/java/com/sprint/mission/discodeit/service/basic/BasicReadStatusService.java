package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public ReadStatusResponse create(ReadStatusCreateRequest request) {
        // 읽기 상태 생성을 위한 필수 검증
        validateCreateReadStatus(request);

        // 읽기 상태 생성 및 저장
        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        readStatusRepository.save(readStatus);

        return ReadStatusResponse.from(readStatus);
    }

    public ReadStatusResponse findById(UUID readStatusId) {
        if (readStatusId == null) {
            throw new RuntimeException("읽기 상태가 존재하지 않습니다.");
        }

        // 읽기 상태가 존재하는지 검색 및 검증
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new RuntimeException("읽기 상태가 존재하지 않습니다."));

        return ReadStatusResponse.from(readStatus);
    }

    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        List<ReadStatusResponse> responses = new ArrayList<>();
        // 유저의 읽기 상태 목록 조회 및 순회
        for (ReadStatus readStatus : readStatusRepository.findAllByUserId(userId)) {
            // 응답 DTO 생성 후 반환 리스트에 추가
            responses.add(ReadStatusResponse.from(readStatus));
        }

        return responses;
    }

    public ReadStatusResponse update(ReadStatusUpdateRequest request) {
        if (request == null || request.id() == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        // 읽기 상태가 존재하는지 검색 및 검증
        ReadStatus readStatus = readStatusRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("읽기 상태가 존재하지 않습니다."));

        // 읽기 상태 업데이트
        Instant newLastReadAt = request.lastReadAt();
        if (newLastReadAt == null) {
            newLastReadAt = Instant.now();
        }
        readStatus.markReadAt(newLastReadAt);

        readStatusRepository.save(readStatus);

        return ReadStatusResponse.from(readStatus);
    }

    public void delete(UUID readStatusId) {
        if (readStatusId == null) {
            throw new RuntimeException("읽기 상태가 존재하지 않습니다.");
        }

        // 읽기 상태가 존재하는지 검색 및 검증
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new RuntimeException("읽기 상태가 존재하지 않습니다."));

        readStatusRepository.delete(readStatusId);
    }

    public void deleteAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 채널이 존재하는지 검색 및 검증
        channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        // 채널의 읽기 상태 모두 삭제
        readStatusRepository.deleteAllByChannelId(channelId);
    }

    private void validateCreateReadStatus(ReadStatusCreateRequest request) {
        if (request == null || request.userId() == null || request.channelId() == null) {
            throw new RuntimeException("요청이 올바르지 않습니다.");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new RuntimeException("채널이 존재하지 않습니다."));

        for (ReadStatus readStatus : readStatusRepository.findAllByUserId(user.getId())) {
            if (readStatus.getChannelId().equals(request.channelId())) {
                throw new RuntimeException("읽기 상태가 이미 존재합니다.");
            }
        }
    }
}
