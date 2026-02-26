package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusPatchDto;
import com.sprint.mission.discodeit.dto.ReadStatusPostDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  private final ReadStatusMapper readStatusMapper;

  public ReadStatusResponseDto create(ReadStatusPostDto readStatusPostDto) {
    userRepository.findById(readStatusPostDto.userId())
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND,
                readStatusPostDto.userId())
        );

    channelRepository.findById(readStatusPostDto.channelId())
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

    return readStatusMapper.toResponseDto(
        readStatusRepository.save(readStatusMapper.fromDto(readStatusPostDto))
    );
  }

  public ReadStatusResponseDto findById(UUID id) {
    return readStatusMapper.toResponseDto(readStatusRepository.findById(id)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND, id))
    );
  }

  public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findByUserId(userId).stream()
        .map(readStatusMapper::toResponseDto)
        .collect(Collectors.toList());
  }

  public ReadStatusResponseDto update(UUID readStatusId, ReadStatusPatchDto readStatusPatchDto) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND, readStatusId));

    // 시간 정보만 최신으로 갱신 후 저장
    readStatus.updateLastReadTime(readStatusPatchDto.newLastReadAt());
    return readStatusMapper.toResponseDto(readStatusRepository.save(readStatus));
  }

  public void delete(UUID id) {
    readStatusRepository.findById(id)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND, id));

    readStatusRepository.delete(id);
  }

}
