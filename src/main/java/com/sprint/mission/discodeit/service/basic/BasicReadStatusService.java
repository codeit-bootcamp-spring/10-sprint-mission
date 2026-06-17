package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.UpdateReadStatusRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.read_status.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.read_status.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDto create(CreateReadStatusRequestDto request) {
        // ID 조회에서 엔티티를 우선 조회하도록 변경
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));
        Channel channel = channelRepository.findById(request.channelId())
            .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));

        boolean exists = readStatusRepository.findAllByUserId(request.userId()).stream()
                .anyMatch(rs -> rs.getChannel().getId().equals(request.channelId()));
        if (exists) {
            throw new ReadStatusAlreadyExistsException(request.userId(), request.channelId());
        }

        ReadStatus readStatus = new ReadStatus(user, channel);
        // 클라이언트가 lastReadAt을 명시적으로 보낸 경우 해당 시점으로 설정
        if (request.lastReadAt() != null) {
            readStatus.updateLastReadAt(request.lastReadAt());
        }
        readStatusRepository.save(readStatus);

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto find(UUID readStatusId) {
        return readStatusMapper.toDto(getReadStatusEntity(readStatusId));
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, UpdateReadStatusRequestDto request) {
        ReadStatus readStatus = getReadStatusEntity(readStatusId);

        Instant requestedTime = request.newLastReadAt();

        if (requestedTime != null) {
            readStatus.updateLastReadAt(requestedTime);
        } else {
            readStatus.updateLastReadAt();
        }

        // 요청에 포함된 경우에만 알림 수신 여부 변경
        if (request.newNotificationEnabled() != null) {
            readStatus.updateNotificationEnabled(request.newNotificationEnabled());
        }

        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = getReadStatusEntity(readStatusId); // 존재 확인 (없으면 예외 발생)
        readStatusRepository.delete(readStatus);
    }

    // 내부 메서드 정의
    private ReadStatus getReadStatusEntity(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
    }
}
