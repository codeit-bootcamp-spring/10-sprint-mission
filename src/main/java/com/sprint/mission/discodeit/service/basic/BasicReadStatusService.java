package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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

@RequiredArgsConstructor
@Service
@Transactional
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public ReadStatus create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UserNotFoundException(userId));
    Channel channel =
        channelRepository
            .findById(channelId)
            .orElseThrow(
                () ->
                    new ChannelNotFoundException(channelId));

    return readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)
            .orElseGet(() ->
                    readStatusRepository.save(new ReadStatus(user, channel, request.lastReadAt())));
  }


  @Transactional(readOnly = true)
  @Override
  public ReadStatus find(UUID readStatusId) {
    return readStatusRepository
        .findById(readStatusId)
        .orElseThrow(
            () ->
                new ReadStatusNotFoundException(readStatusId));
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReadStatus> findAllByUser_Id(UUID userId) {
    return readStatusRepository.findAllByUser_Id(userId).stream()
        .toList();
  }

  @Override
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus =
        readStatusRepository
            .findById(readStatusId)
            .orElseThrow(
                () ->
                    new ReadStatusNotFoundException(readStatusId));

    readStatus.update(request.newLastReadAt());
    //Dirty checking
    return readStatus;
  }

  @Override
  public void delete(UUID readStatusId) {
    ReadStatus readStatus =
        readStatusRepository
            .findById(readStatusId)
            .orElseThrow(
                () ->
                    new ReadStatusNotFoundException(readStatusId));
    readStatusRepository.delete(readStatus);
  }
}
