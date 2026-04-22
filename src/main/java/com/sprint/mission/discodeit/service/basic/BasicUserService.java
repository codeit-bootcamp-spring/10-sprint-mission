package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.logging.ServiceLogAround;
import com.sprint.mission.discodeit.dto.FileUploadDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.publisher.FileUploadEventPublisher;
import com.sprint.mission.discodeit.exception.user.UserErrorCode;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final UserStatusMapper userStatusMapper;
  private final BasicDomainTemplate domainTemplate;
  private final FileUploadEventPublisher fileUploadEventPublisher;

  @Override
  @ServiceLogAround
  @Transactional(readOnly = true)
  public UserDto find(UUID id) {
    User user = findById(id);
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAllUsersProfileAndStatusBy();
    return userMapper.toDto(users);
  }

  @Override
  @ServiceLogAround
  public UserDto create(UserCreateRequest request, Optional<FileUploadDto> profile) {
    validateUserUniqueness(request.getUsername(), request.getEmail());
    Optional<BinaryContent> binaryContent = binaryContentMapper.toEntityFrom(profile);
    fileUploadEventPublisher.publishFileUploadEvent(binaryContent, profile);
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
  @ServiceLogAround
  public UserDto update(UUID id, UserUpdateRequest request, Optional<FileUploadDto> profile) {
    validateUserUniqueness(request.getUsername(), request.getEmail());
    User user = findById(id);
    Optional<BinaryContent> binaryContent = binaryContentMapper.toEntityFrom(profile);
    fileUploadEventPublisher.publishFileUploadEvent(binaryContent, profile);
    userMapper.partialUpdate(request, binaryContent, user);
    return userMapper.toDto(user);
  }

  @Override
  @ServiceLogAround
  public void delete(UUID id) {
    domainTemplate.deleteByIdOrThrow(id, userRepository,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  private User findById(UUID id) {
    return domainTemplate.getOrThrow(id, userRepository::findProfileAndStatusById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  // todo: distinguish create and update
  private void validateUserUniqueness(String username, String email) {
    if (username != null) {
      domainTemplate.throwOrNot(username, Predicate.not(userRepository::existsByUsername),
          value -> new UserException(UserErrorCode.USERNAME_ALREADY_EXIST,
              Map.of("username", value)));
    }
    if (email != null) {
      domainTemplate.throwOrNot(email, Predicate.not(userRepository::existsByEmail),
          value -> new UserException(UserErrorCode.EMAIL_ALREADY_EXIST,
              Map.of("email", value)));
    }
  }
}
