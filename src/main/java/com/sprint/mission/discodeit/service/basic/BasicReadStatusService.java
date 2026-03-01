package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    validateUserAndChannelExists(request.userId(), request.channelId());

    // 해당 채널에 참여 중인지 확인
    readStatusRepository.findAllByUserId(request.userId()).stream()
        .filter(readStatus -> readStatus.getChannelId().equals(request.channelId()))
        .findAny()
        .ifPresent(readStatus -> {
          throw new IllegalStateException("이미 해당 채널에 참여 중인 유저입니다.");
        });

    ReadStatus readStatus = new ReadStatus(
        request.userId(), request.channelId(), null);
    readStatusRepository.save(readStatus);

    return toDto(readStatus);
  }

  @Override
  public ReadStatusDto findById(UUID id) {
    ReadStatus readStatus = getOrThrowReadStatus(id);
    return toDto(readStatus);
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public ReadStatusDto update(UUID id, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = getOrThrowReadStatus(id);

    if (request.newLastReadAt() != null) {
      readStatus.updateLastReadAt(request.newLastReadAt());
    }

    readStatusRepository.save(readStatus);
    return toDto(readStatus);
  }

  @Override
  public void deleteById(UUID id) {
    getOrThrowReadStatus(id);
    readStatusRepository.deleteById(id);
  }


  // 참여 정보 검증
  private ReadStatus getOrThrowReadStatus(UUID id) {
    return readStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 참여 정보를 찾을 수 없습니다."));
  }

  // 유저, 채널 검증
  private void validateUserAndChannelExists(UUID userId, UUID channelId) {
    if (userRepository.findById(userId).isEmpty()) {
      throw new NoSuchElementException("존재하지 않는 유저입니다.");
    }
    if (channelRepository.findById(channelId).isEmpty()) {
      throw new NoSuchElementException("존재하지 않는 채널입니다");
    }
  }

  // 엔티티 -> DTO 변환
  private ReadStatusDto toDto(ReadStatus readStatus) {
    return new ReadStatusDto(
        readStatus.getId(),
        readStatus.getCreatedAt(),
        readStatus.getUpdatedAt(),
        readStatus.getUserId(),
        readStatus.getChannelId(),
        readStatus.getLastReadAt()
    );
  }
}
