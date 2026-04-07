package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.dto.UserStatusDto;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import com.sprint.mission.discodeit.user.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.user.repository.JPAUserRepository;
import com.sprint.mission.discodeit.user.repository.JPAUserStatusRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final JPAUserStatusRepository jpaUserStatusRepository;
  private final JPAUserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public UserStatusDto create(UserStatusCreateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

    jpaUserStatusRepository.findByUser(user)
        .ifPresent(status -> {
          throw new IllegalArgumentException("해당 유저의 접속 상태 객체가 이미 존재합니다.");
        });

    UserStatus userStatus = jpaUserStatusRepository.save(new UserStatus(user));
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto find(UUID userStatusID) {
    return jpaUserStatusRepository.findById(userStatusID)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("해당 접속 상태 객체를 찾을 수 없습니다."));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatusDto> findAll() {
    return jpaUserStatusRepository.findAll()
        .stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus userStatus = jpaUserStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException(("해당 유저의 접속 상태 객체를 찾을 수 없습니다.")));
    userStatus.updateConnection(request.newLastActiveAt());
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus userStatus = jpaUserStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException(("해당 유저의 접속 상태 객체를 찾을 수 없습니다.")));

    userStatus.updateConnection(request.newLastActiveAt());
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional
  public void delete(UUID userStatusId) {
    UserStatus userStatus = jpaUserStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException(("해당 접속 상태 객체를 찾을 수 없습니다.")));
    jpaUserStatusRepository.delete(userStatus);
  }
}

