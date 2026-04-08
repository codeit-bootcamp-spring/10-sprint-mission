package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusPatchDto;
import com.sprint.mission.discodeit.dto.ReadStatusPostDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final ReadStatusMapper readStatusMapper;

    public ReadStatusDto create(ReadStatusPostDto readStatusPostDto) {
        User user = userRepository.findById(readStatusPostDto.userId())
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND,
                    readStatusPostDto.userId())
            );

        Channel channel = channelRepository.findById(readStatusPostDto.channelId())
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND,
                    readStatusPostDto.channelId())
            );

        // 이미 관련 객체가 존재하면 예외 발생
        readStatusRepository.findByUserIdAndChannelId(readStatusPostDto.userId(),
                readStatusPostDto.channelId())
            .ifPresent(rs -> {
                throw new BusinessLogicException(ExceptionCode.DUPLICATED_READ_STATUS,
                    readStatusPostDto.userId(),
                    readStatusPostDto.channelId());
            });

        ReadStatus readStatus = new ReadStatus(user, channel, readStatusPostDto.lastReadAt());
        readStatusRepository.save(readStatus);

        return readStatusMapper.toResponseDto(readStatus);
    }

    @Transactional(readOnly = true)
    public ReadStatusDto findById(UUID id) {
        return readStatusMapper.toResponseDto(readStatusRepository.findById(id)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND, id))
        );
    }

    @Transactional(readOnly = true)
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findByUserId(userId).stream()
            .map(readStatusMapper::toResponseDto)
            .toList();
    }

    public ReadStatusDto update(UUID readStatusId, ReadStatusPatchDto readStatusPatchDto) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND,
                    readStatusId));

        // 시간 정보만 최신으로 갱신 후 저장
        readStatus.updateLastReadTime(readStatusPatchDto.newLastReadAt());
        return readStatusMapper.toResponseDto(readStatus);
    }

    public void delete(UUID id) {
        if (!readStatusRepository.existsById(id)) {
            throw new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND, id);
        }

        readStatusRepository.deleteById(id);
    }

}
