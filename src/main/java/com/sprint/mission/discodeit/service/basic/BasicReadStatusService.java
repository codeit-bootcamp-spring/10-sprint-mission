package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateDto dto) {
        if (!userRepository.existsById(dto.getUserId()) ||
                !channelRepository.existsById(dto.getChannelId())) {
            throw new NoSuchElementException("유저 또는 사용자를 찾을 수 없습니다.");
        }

        readStatusRepository.findByUserIdAndChannelId(dto.getUserId(), dto.getChannelId())
                .ifPresent(rs -> { throw new IllegalStateException("이미 존재합니다."); });

        ReadStatus readStatus = new ReadStatus(dto.getUserId(), dto.getChannelId());
        readStatusRepository.save(readStatus);
        return convertToDto(readStatus);
    }

    @Override
    public ReadStatusResponseDto find(UUID id) {
        return readStatusRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다."));
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateDto dto) {
        ReadStatus readStatus = readStatusRepository.findById(dto.getId())
                .orElseThrow(() -> new NoSuchElementException("ReadStatus를 찾을 수 없습니다."));

        if (dto.getLastReadAt() != null) {
            readStatus.updateLastReadAt(dto.getLastReadAt());
        }

        readStatusRepository.save(readStatus);
        return convertToDto(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.deleteById(id);
    }

    private ReadStatusResponseDto convertToDto(ReadStatus rs) {
        return new ReadStatusResponseDto(rs.getId(), rs.getUserId(), rs.getChannelId(), rs.getLastReadAt());
    }
}