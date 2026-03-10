package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
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
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserStatus create(UUID userId) {
    // 관련 유저가 존재하는지 확인
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

    // 해당 유저의 UserStatus가 존재하는지 확인
    if (userStatusRepository.existsByUserId(userId)) {
      throw new IllegalStateException("이미 상태 정보가 존재합니다.");
    }

    UserStatus userStatus = new UserStatus(user, Instant.now());
    return userStatusRepository.save(userStatus);
  }

  @Override
  public UserStatus findById(UUID id) {
    return getOrThrowUserStatus(id);
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  @Transactional
  public UserStatus updateByUserId(UUID id, Instant newLastActiveAt) {
    UserStatus userStatus = getOrThrowUserStatus(id);
    
    if (newLastActiveAt != null) {
      userStatus.updateLastActiveAt(newLastActiveAt);
    }

    return userStatus;
  }

  @Override
  public void deleteById(UUID id) {
    UserStatus userStatus = getOrThrowUserStatus(id);
    userStatusRepository.delete(userStatus);
  }

  // --- Helper Methods ---

  private UserStatus getOrThrowUserStatus(UUID id) {
    return userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 상태 정보를 찾을 수 없습니다."));
  }
}
