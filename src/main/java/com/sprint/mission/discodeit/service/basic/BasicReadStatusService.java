package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatusDto.Response create(ReadStatusDto.Create request) {

    userRepository.findById(request.userId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    channelRepository.findById(request.channelId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));

    readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
        .ifPresent(status -> {
          throw new BusinessLogicException(ExceptionCode.READ_STATUS_ALREADY_EXISTS);
        });

    ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
    readStatusRepository.save(readStatus);
    return ReadStatusDto.Response.of(readStatus);
  }

  @Override
  public ReadStatusDto.Response findById(UUID statusId) {
    ReadStatus status = readStatusRepository.findById(statusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND));
    return ReadStatusDto.Response.of(status);
  }

  @Override
  public List<ReadStatusDto.Response> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatusDto.Response::of)
        .toList();
  }

  @Override
  public ReadStatusDto.Response update(UUID readStatusId, ReadStatusDto.Update request) {
    ReadStatus status = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND));
    status.updateLastReadAt(request.newLastReadAt());
    readStatusRepository.save(status);
    return ReadStatusDto.Response.of(status);
  }

  @Override
  public void delete(UUID statusId) {
    ReadStatus status = readStatusRepository.findById(statusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.READ_STATUS_NOT_FOUND));
    readStatusRepository.delete(status);
  }
}
