package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  @Override
  @Transactional
  public ReadStatus create(UUID userId, UUID channelId, Instant lastReadAt) {
    User user = getOrThrowUser(userId);
    Channel channel = getOrThrowChannel(channelId);

    // 해당 채널에 참여 중인지 확인
    readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
        .ifPresent(rs -> {
          throw new IllegalStateException("이미 해당 채널에 참여 중인 유저입니다.");
        });

    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    return readStatusRepository.save(readStatus);
  }

  @Override
  public ReadStatus findById(UUID id) {
    return getOrThrowReadStatus(id);
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId);
  }

  @Override
  @Transactional
  public ReadStatus update(UUID id, Instant newLastReadAt) {
    ReadStatus readStatus = getOrThrowReadStatus(id);

    if (newLastReadAt != null) {
      readStatus.updateLastReadAt(newLastReadAt);
    }

    return readStatus;
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    ReadStatus readStatus = getOrThrowReadStatus(id);
    readStatusRepository.delete(readStatus);
  }

  // --- Helper Methods ---

  // 유저 검증
  private User getOrThrowUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
  }

  // 채널 검증
  private Channel getOrThrowChannel(UUID id) {
    return channelRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다."));
  }

  // 참여 정보 검증
  private ReadStatus getOrThrowReadStatus(UUID id) {
    return readStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 참여 정보를 찾을 수 없습니다."));
  }
}
