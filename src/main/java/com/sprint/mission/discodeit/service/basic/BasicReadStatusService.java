package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    public ReadStatusDto create(ReadStatusCreateRequest readStatusCreateRequest) {
        UUID userId = readStatusCreateRequest.userId();
        UUID channelId = readStatusCreateRequest.channelId();
        Instant lastReadAt = readStatusCreateRequest.lastReadAt();
        // 관련된 Channel이나 User가 존재하지 않으면 예외 발생
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException(channelId+"를 가진 채널은 존재하지 않습니다"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException((userId+"를 가진 유저는 존재하지 않습니다")));

        // 같은 Channel과 User와 관련된 객체가 이미 존재하면 예외를 발생
        if (readStatusRepository.existsByChannelAndUser(channel, user)) {
            throw new IllegalArgumentException("이미 channelId:"+channelId+", userId:"+userId+"와 관련된 객체가 존재합니다");
        }
        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
        return readStatusMapper.toDto(readStatusRepository.save(readStatus));
    }

    @Override
    @Transactional(readOnly = true)
    public ReadStatusDto find(UUID readStatusId) {
        ReadStatus readStatus = getReadStatusByIdOrThrow(readStatusId);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Override
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatus readStatus = getReadStatusByIdOrThrow(readStatusId);
        Instant lastReadAt = readStatusUpdateRequest.newLastReadAt();
        readStatus.updateLastReadAt(lastReadAt);
        return readStatusMapper.toDto(readStatusRepository.save(readStatus));
    }

    @Override
    public void delete(UUID readStatusId) {
        if(!readStatusRepository.existsById(readStatusId)) {
            throw new NoSuchElementException(readStatusId+"를 가진 ReadStatus를 찾지 못했습니다");
        }
        readStatusRepository.deleteById(readStatusId);
    }

    // ReadStatusRepository.findById()를 통한 반복되는 ReadStatus 조회/예외처리를 중복제거 하기 위한 메서드
    private ReadStatus getReadStatusByIdOrThrow(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new NoSuchElementException("readStatusId:"+readStatusId+"를 가진 ReadStatus를 찾지 못했습니다"));
    }
}
