package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusPatchDto;
import com.sprint.mission.discodeit.dto.UserStatusPostDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  private final UserStatusMapper userStatusMapper;

  public UserStatusResponseDto create(UserStatusPostDto userStatusPostDto) {
    User user = userRepository.findById(userStatusPostDto.userId())
        .orElseThrow(() ->
            new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userStatusPostDto.userId())
        );

    userStatusRepository.findByUserId(user.getId())
        .ifPresent(ut -> {
          throw new BusinessLogicException(ExceptionCode.USER_STATUS_DUPLICATED);
        });

    return userStatusMapper.toResponseDto(
        userStatusRepository.save(new UserStatus(userStatusPostDto.userId()))
    );
  }

  public UserStatusResponseDto findById(UUID id) {
    return userStatusMapper.toResponseDto(
        userStatusRepository.findById(id)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND))
    );
  }

  public List<UserStatusResponseDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toResponseDto)
        .collect(Collectors.toList());
  }

  public UserStatusResponseDto updateByUserId(UUID userId, UserStatusPatchDto userStatusPatchDto) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() ->
            new BusinessLogicException(ExceptionCode.USER_STATUS_WITH_USER_ID_NOT_FOUND, userId)
        );

    userStatus.updateLastAccessedTime(userStatusPatchDto.newLastActiveAt());
    return userStatusMapper.toResponseDto(userStatusRepository.save(userStatus));
  }

  public void delete(UUID id) {
    userStatusRepository.findById(id).ifPresentOrElse(
        value -> userStatusRepository.delete(id),
        () -> {
          throw new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND, id);
        }
    );
  }

}
