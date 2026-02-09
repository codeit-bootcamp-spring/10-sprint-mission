package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ReadStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {
    // 필드
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatusResponse create(ReadStatusCreateRequest request){
        ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
        ReadStatus newReadStatus = readStatusRepository.save(readStatus);
        return new ReadStatusResponse(newReadStatus.getId(), newReadStatus.getLastReadTime());
    }

    @Override
    public ReadStatusResponse find(UUID readStatusID){
        ReadStatus readStatus = readStatusRepository.find(readStatusID)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusID));

        return new ReadStatusResponse(readStatus.getId(), readStatus.getLastReadTime());
    }

    @Override
    public List<ReadStatusResponse> findAllByUserID(UUID userID){
        return readStatusRepository.findByUserID(userID).stream()
                .map(rs -> new ReadStatusResponse(
                        rs.getId(),
                        rs.getLastReadTime()
                ))
                .toList();
    }

    @Override
    public ReadStatusResponse update(UUID readStatusId, ReadStatusUpdateRequest request){
        ReadStatus readStatus = readStatusRepository.find(readStatusId)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusId));

        readStatus.updateLastReadTime();
        ReadStatus newReadStatus = readStatusRepository.save(readStatus);

        return new ReadStatusResponse(newReadStatus.getId(), newReadStatus.getLastReadTime());
    }

    @Override
    public void delete(UUID readStatusID){
        ReadStatus readStatus = readStatusRepository.find(readStatusID)
                .orElseThrow(() -> new IllegalArgumentException("ReadStatus not found: " + readStatusID));

        readStatusRepository.delete(readStatus.getId());
    }
}
