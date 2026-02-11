package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequestDTO;
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
    public ReadStatusResponseDTO create(ReadStatusCreateRequestDTO readStatusCreateRequestDTO) {
        UUID userId = readStatusCreateRequestDTO.userId();
        UUID channelId = readStatusCreateRequestDTO.channelId();
        Instant lastReadTime = readStatusCreateRequestDTO.lastReadTime();
        // 관련된 Channel이나 User가 존재하지 않으면 예외 발생
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException(channelId+"를 가진 채널은 존재하지 않습니다");
        }
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(userId+"를 가진 유저는 존재하지 않습니다");
        }
        // 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생
        if (readStatusRepository.findAllByChannelId(channelId)
                .stream()
                .anyMatch(readStatus -> readStatus.getUserId().equals(userId))) {
            throw new IllegalStateException("이미 channelId:"+channelId+", userId:"+userId+"와 관련된 객체가 존재합니다");
        }
        ReadStatus readStatus = new ReadStatus(userId, channelId, lastReadTime);
        return toReadStatusResponseDTO(readStatusRepository.save(readStatus));
    }

    @Override
    public ReadStatusResponseDTO find(UUID readStatusId) {
        ReadStatus readStatus = getReadStatusByIdOrThrow(readStatusId);
        return toReadStatusResponseDTO(readStatus);
    }

    @Override
    public List<ReadStatusResponseDTO> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(this::toReadStatusResponseDTO)
                .toList();
    }

    @Override
    public ReadStatusResponseDTO update(UUID readStatusId, ReadStatusUpdateRequestDTO readStatusUpdateRequestDTO) {
        ReadStatus readStatus = getReadStatusByIdOrThrow(readStatusId);
        Instant lastReadTime = readStatusUpdateRequestDTO.lastReadTime();
        readStatus.updateLastReadTime(lastReadTime);
        return toReadStatusResponseDTO(readStatusRepository.save(readStatus));
    }

    @Override
    public void delete(UUID readStatusId) {
        if(!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException(readStatusId+"를 가진 ReadStatus를 찾지 못했습니다");
        }
        readStatusRepository.deleteById(readStatusId);
    }
    
    // 간단한 응답용 DTO를 만드는 메서드
    private ReadStatusResponseDTO toReadStatusResponseDTO(ReadStatus readStatus) {
        return new ReadStatusResponseDTO(
                readStatus.getId(),
                readStatus.getUserId(),
                readStatus.getChannelId(),
                readStatus.getLastReadTime()
        );
    }

    // ReadStatusRepository.findById()를 통한 반복되는 ReadStatus 조회/예외처리를 중복제거 하기 위한 메서드
    private ReadStatus getReadStatusByIdOrThrow(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("readStatusId:"+readStatusId+"를 가진 ReadStatus를 찾지 못했습니다"));
    }
}
