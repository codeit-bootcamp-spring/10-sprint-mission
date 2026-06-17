package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ReadStatusMapper readStatusMapper;

    // 읽음 상태 생성
    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusCreateRequest readStatusCreateRequest) {
        UserEntity targetUser = getUserEntityOrThrow(readStatusCreateRequest.userId());
        ChannelEntity targetChannel = getChannelEntityOrThrow(readStatusCreateRequest.channelId());

        // 유효성 검증 (중복 확인)
        existsByUserIdAndChannelId(targetUser.getId(), targetChannel.getId());

        ReadStatusEntity newReadStatus = new ReadStatusEntity(targetUser, targetChannel, readStatusCreateRequest.lastReadAt());
        readStatusRepository.save(newReadStatus);

        return readStatusMapper.toDto(newReadStatus);
    }

    // 읽음 상태 단건 조회
    @Override
    public ReadStatusDto findById(UUID readStatusId) {
        ReadStatusEntity targetReadStatus = getReadStatusEntity(readStatusId);

        return readStatusMapper.toDto(targetReadStatus);
    }

    // 읽음 상태 전체 조회
    @Override
    public List<ReadStatusDto> findAll() {
        return readStatusRepository.findAllWithDetails().stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    // 특정 사용자의 읽음 상태 조회
    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        UserEntity targetUser = getUserEntityOrThrow(userId);

        return readStatusRepository.findAllByUser(targetUser).stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    // 읽음 상태 수정
    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest readStatusUpdateRequest) {
        ReadStatusEntity targetReadStatus = getReadStatusEntity(readStatusId);

        targetReadStatus.updateLastReadTime(readStatusUpdateRequest.newLastReadAt());
        targetReadStatus.updateNotificationEnabled(readStatusUpdateRequest.notificationEnabled());

        return readStatusMapper.toDto(targetReadStatus);
    }

    // 읽음 상태 삭제
    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        ReadStatusEntity targetReadStatus = getReadStatusEntity(readStatusId);

        readStatusRepository.delete(targetReadStatus);
    }

    // 사용자 반환
    private UserEntity getUserEntityOrThrow(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // 채널 반환
    private ChannelEntity getChannelEntityOrThrow(UUID channelId){
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));
    }

    // 읽음 상태 반환
    private ReadStatusEntity getReadStatusEntity(UUID readStatusId){
        return readStatusRepository.findBysIdWithDetails(readStatusId)
                .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
    }

    // 유효성 검사 (중복 확인)
    private void existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (readStatusRepository.existsByUserIdAndChannelId(userId, channelId)) {
            throw new DuplicateReadStatusException(userId, channelId);
        }
    }
}