package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.common.CommonErrorCode;
import com.sprint.mission.discodeit.exception.common.CommonException;
import com.sprint.mission.discodeit.exception.user.UserErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService extends BasicDomainService<User> implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID id) {
    User user = findById(id);
    log.debug("[USER] id={}, username={}, email={}",
        user.getId(), user.getUsername(), user.getEmail());
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll();
    log.debug("[USERS] count={}", users.size());
    return userMapper.toDto(users);
  }

  @Override
  public UserDto create(UserCreateRequest request, MultipartFile profile) {
    validateUserUniqueness(request.getUsername(), request.getEmail());
    log.debug("[USER] Unique: username={}, email={}",
        request.getUsername(), request.getEmail());
    BinaryContent binaryContent = getBinaryContent(profile);
    User user = userMapper.toEntityFrom(request, userStatusMapper.createDefault(), binaryContent);
    userRepository.save(user);
    return userMapper.toDto(user);
  }

  /*
   * todo: Regarding profile image updates.
   *  If an image already exists and the received image is null,
   *  distinguish whether to delete the image or not update it.
   * */
  @Override
  public UserDto update(UUID id, UserUpdateRequest request, MultipartFile profile) {
    validateUserUniqueness(request.getUsername(), request.getEmail());
    User user = findById(id);
    BinaryContent binaryContent = getBinaryContent(profile);
    userMapper.partialUpdate(request, binaryContent, user);
    return userMapper.toDto(user);
  }

  @Override
  public void delete(UUID id) {
    deleteByIdOrThrow(id, userRepository,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  @Override
  protected User findById(UUID id) {
    return getOrThrow(id, userRepository::findById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  // todo: distinguish create and update
  private void validateUserUniqueness(String username, String email) {
    if (username != null) {
      ensure(username, userRepository::existsByUsername,
          value -> new UserException(UserErrorCode.USERNAME_ALREADY_EXIST,
              Map.of("username", value)));
    }
    if (email != null) {
      ensure(email, userRepository::existsByEmail,
          value -> new UserException(UserErrorCode.EMAIL_ALREADY_EXIST,
              Map.of("email", value)));
    }
  }

  private BinaryContent getBinaryContent(MultipartFile profile) {
    try {
      return binaryContentMapper.toEntityFrom(profile);
    } catch (IOException e) {
      log.error("[USER] profile image can't be loaded");
      throw new CommonException(CommonErrorCode.FILE_CANT_READ, profile);
    }
  }
}
