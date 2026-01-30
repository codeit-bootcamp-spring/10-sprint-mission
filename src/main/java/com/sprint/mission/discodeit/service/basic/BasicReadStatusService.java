package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.ReadStatusInfoDto;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusInfoDto create(ReadStatusCreateDto readStatusCreateDto) {
        // Channel, User 존재 여부 검증
        userRepository.findById(readStatusCreateDto.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

        channelRepository.findById(readStatusCreateDto.channelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));

        // 중복된 데이터 검증
        if(readStatusRepository.existsByUserIdAndChannelId(readStatusCreateDto.userId(), readStatusCreateDto.channelId())){
            throw new IllegalArgumentException("이미 해당 객체가 존재합니다.");
        }

        ReadStatus readStatus = new ReadStatus(readStatusCreateDto.userId(), readStatusCreateDto.channelId());
        readStatusRepository.save(readStatus);
        return readStatusMapper.toReadStatusInfoDto(readStatus);
    }

    @Override
    public ReadStatusInfoDto findById(UUID id) {
        return readStatusMapper
                .toReadStatusInfoDto(
                        readStatusRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 ReadStatus가 없습니다.")));
    }

    @Override
    public List<ReadStatusInfoDto> findAllByUserId(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));

        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toReadStatusInfoDto)
                .toList();
    }

    @Override
    public ReadStatusInfoDto update(ReadStatusUpdateDto readStatusUpdateDto) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusUpdateDto.id()).orElseThrow(() -> new IllegalArgumentException("해당 ReadStatus가 없습니다."));
        readStatus.updateLastReadAt(readStatusUpdateDto.lastReadAt());
        readStatusRepository.save(readStatus);
        return readStatusMapper.toReadStatusInfoDto(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ReadStatus가 없습니다."));
        readStatusRepository.delete(id);
    }


}
