package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;
    private final UserStatusRepository userStatusRepository;

    @Override
    public ReadStatusResponseDto create(ReadStatusCreateDto dto) {
        // userId, channelId가 null이라면 예외
        if(dto.getUserId() == null || dto.getChannelId() == null){
            throw new NoSuchElementException("사용자나 채널이 존재하지 않습니다.");
        }
        // channelId, userId가 중복된 객체가 있는지 확인
        List<ReadStatus> duplicateList = readStatusRepository.findAll().stream()
                .filter(readStatus ->
                        readStatus.getChannelId().equals(dto.getChannelId()) &&
                                readStatus.getUserId().equals(dto.getUserId()))
                .toList();

        // 중복된게 없다면 생성
        if(duplicateList.isEmpty()){
            ReadStatus readStatus = new ReadStatus(dto.getUserId(),dto.getChannelId());
            readStatusRepository.save(readStatus);
            // dto 반환
            return readStatusMapper.toDto(readStatus);
        }
        // 있다면 예외처리
        else {
            throw new IllegalStateException("중복된 객체가 있습니다");
        }
    }

    @Override
    public ReadStatusResponseDto findReadStatus(UUID id) {
        ReadStatus readStatus = getReadStatus(id);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusResponseDto> findAll() {
        List<ReadStatus> readStatusList = readStatusRepository.findAll();
        return readStatusList.stream()
                                        .map(readStatusMapper::toDto)
                                        .toList();

    }

    @Override
    public ReadStatusResponseDto update(ReadStatusUpdateDto dto) {
        ReadStatus readStatus = getReadStatus(dto.getId());
        readStatus.updateReadTime();
        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public void delete(UUID id) {
        readStatusRepository.delete(id);

    }

    private ReadStatus getReadStatus(UUID id){
        return readStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 읽기 상태는 없습니다."));
    }
}
