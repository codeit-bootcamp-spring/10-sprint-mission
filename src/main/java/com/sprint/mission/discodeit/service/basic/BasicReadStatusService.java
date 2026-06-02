package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    public ReadStatusDto.Response create(ReadStatusDto.CreateRequest request) {
        UUID userId = request.userId();
        UUID channelId =  request.channelId();
        Instant lastReadAt = request.lastReadAt();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

        boolean isExist = readStatusRepository.existsByUserIdAndChannelId(userId, channelId);
        if(isExist) throw ReadStatusAlreadyExistsException.withUserAndChannel(userId, channelId);

        ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);

        try { // 레이스 컨디션으로 이미 데이터가 생성된 경우, 기존 데이터를 조회하여 반환함으로써 멱등성 보장
            return readStatusMapper.toResponse(readStatusRepository.saveAndFlush(readStatus));
        } catch (DataIntegrityViolationException e) {
            ReadStatus existingStatus = readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
                    .orElseThrow(() -> InternalServerException.dataIntegrity("ReadStatus가 존재해야 함에도 찾을 수 없음: User %s, Channel %s", user.getId(), channel.getId()));
            return readStatusMapper.toResponse(existingStatus);
        }
    }

    @Override
    public ReadStatusDto.Response find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId)
                .map(readStatusMapper::toResponse)
                .orElseThrow(() -> ReadStatusNotFoundException.withId(readStatusId));
    }

    @Override
    public List<ReadStatusDto.Response> findAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }
        return readStatusRepository.findAllByUserId(userId).stream()
                .map(readStatusMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusDto.Response update(UUID readStatusId, ReadStatusDto.UpdateRequest request) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> ReadStatusNotFoundException.withId(readStatusId));

        readStatus.update(request.newLastReadAt());

        return readStatusMapper.toResponse(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
                .orElseThrow(() -> ReadStatusNotFoundException.withId(readStatusId));

        readStatusRepository.delete(readStatus);
    }
}
