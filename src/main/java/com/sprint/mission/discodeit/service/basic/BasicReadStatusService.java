package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
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
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusDto create(CreateReadStatusRequestDto dto) {
        // 존재 여부 검증
        if (!userRepository.existsById(dto.getUserId())) {
            throw new NoSuchElementException("User not found with id " + dto.getUserId());
        }
        if (!channelRepository.existsById(dto.getChannelId())) {
            throw new NoSuchElementException("Channel not found with id " + dto.getChannelId());
        }

        // 이미 객체가 존재하는지 검사(읽은 적이 있는지)
        boolean exists = readStatusRepository.findAllByUserId(dto.getUserId()).stream()
                .anyMatch(rs -> rs.getChannelId().equals(dto.getChannelId()));
        if (exists) {
            throw new IllegalArgumentException("ReadStatus already exists for this user and channel.");
        }

        ReadStatus readStatus = new ReadStatus(dto.getUserId(), dto.getChannelId());
        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto find(UUID readStatusId) {
        ReadStatus readStatus = getReadStatusEntity(readStatusId);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id " + userId);
        }
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatusDto update(UUID readStatusId, UpdateReadStatusRequestDto request) {
        ReadStatus readStatus = getReadStatusEntity(readStatusId);

        readStatus.updateLastReadAt();
        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public void delete(UUID readStatusId) {
        getReadStatusEntity(readStatusId); // 존재 확인 (없으면 예외 발생)
        readStatusRepository.deleteById(readStatusId);
    }

    // ----------------------------------------------------
    // 내부 메서드 정의

    private ReadStatus getReadStatusEntity(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus not found with id " + readStatusId));
    }
}
