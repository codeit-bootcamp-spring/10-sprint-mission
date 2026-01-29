package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public ReadStatus create(ReadStatusDto.CreateRequest request) {
        UUID userId = request.userId();
        UUID channelId =  request.channelId();
        Instant lastReadAt = request.lastReadAt();

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("해당 유저를 찾을 수 없습니다:" + userId);
        }
        if (!channelRepository.existsById(channelId)) {
            throw new RuntimeException("해당 채널을 찾을 수 없습니다: " + channelId);
        }
        boolean isExist = readStatusRepository.findAllByChannelId(channelId).stream()
                .anyMatch(readStatus -> readStatus.getUserId().equals(userId));
        if(isExist) throw new RuntimeException("이미 해당 유저와 채널 간 ReadStatus가 존재합니다.");

        ReadStatus readStatus = new ReadStatus(userId, channelId, lastReadAt);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public ReadStatus find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus를 찾을 수 없습니다: " + readStatusId));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    public ReadStatus update(UUID readStatusId, ReadStatusDto.UpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("해당 ReadStatus를 찾을 수 없습니다: " + readStatusId));

        readStatus.update(request.newLastReadAt());

        return readStatusRepository.save(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        if (!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException("해당 ReadStatus를 찾을 수 없습니다: " + readStatusId);
        }
        readStatusRepository.deleteById(readStatusId);
    }
}
