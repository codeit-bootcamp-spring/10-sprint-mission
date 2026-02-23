package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public UserDto.Response create(UserDto.Create request, MultipartFile file) {
    existsByUsername(request.username());
    existsByEmail(request.email());

    //프로필 설정하지 않으면 null
    UUID profileId = null;

    //요청에 프로필이 있다면 binaryContent 객체 생성 후 저장
    if (file != null && !file.isEmpty()) {
      try {
        BinaryContent profile = new BinaryContent(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            file.getBytes()
        );
        binaryContentRepository.save(profile);
        profileId = profile.getId();
      } catch (IOException e) {
        throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_UPLOAD_FAILED);
      }
    }

    User user = new User(
        request.username(),
        request.email(),
        request.password(),
        profileId
    );
    userRepository.save(user);

    //유저 상태 객체 생성 후 저장
    UserStatus status = new UserStatus(user.getId());
    userStatusRepository.save(status);

    return UserDto.Response.of(user, status);
  }

  @Override
  public UserDto.Response findById(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    UserStatus status = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    return UserDto.Response.of(user, status);
  }

  @Override
  public List<UserDto.Response> findAll() {
    return userRepository.findAll().stream()
        .map(user -> {
          UserStatus status = userStatusRepository.findByUserId(user.getId())
              .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
          return UserDto.Response.of(user, status);
        })
        .toList();
  }

  @Override
  public UserDto.Response update(UUID userId, UserDto.Update request, MultipartFile file) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    Optional.ofNullable(request.newUsername()).ifPresent(user::updateUsername);
    Optional.ofNullable(request.newEmail()).ifPresent(user::updateEmail);
    Optional.ofNullable(request.newPassword()).ifPresent(user::updatePassword);

    if (file != null && !file.isEmpty()) { //요청에 프로필 파일이 있는지 확인
      if (user.getProfileId() != null) { //기존 유저에게 프로필이 있는지 확인, 프로필이 있으면 지움
        binaryContentRepository.findById(user.getProfileId())
            .ifPresent(binaryContentRepository::delete);
      }

      try {
        BinaryContent newProfile = new BinaryContent(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            file.getBytes()
        );
        binaryContentRepository.save(newProfile);
        user.updateProfileId(newProfile.getId());
      } catch (IOException e) {
        throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_UPLOAD_FAILED);
      }

    }
    //프로필 말고 다른 변경사항이 있을 수 있으니 if문 밖에서 저장
    userRepository.save(user);

    //유저 상태 객체 획인
    UserStatus status = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));

    userStatusRepository.save(status);
    return UserDto.Response.of(user, status);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    //유저가 작성한 메시지 목록
    List<Message> messages = messageRepository.findAll().stream()
        .filter(message -> message.getAuthorId().equals(userId))
        .toList();
    //메시지 속 첨부파일 삭제, 메시지 삭제
    for (Message message : messages) {
      for (UUID attachmentId : message.getAttachmentIds()) {
        binaryContentRepository.findById(attachmentId)
            .ifPresent(binaryContentRepository::delete);
      }
      messageRepository.delete(message);
    }

    //읽음 상태 삭제
    readStatusRepository.findAllByUserId(userId)
        .forEach(readStatusRepository::delete);

    //유저 상태 삭제
    UserStatus status = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    userStatusRepository.delete(status);

    //유저 프로필 삭제
    Optional.ofNullable(user.getProfileId())
        .flatMap(binaryContentRepository::findById)
        .ifPresent(binaryContentRepository::delete);

    userRepository.delete(user);
  }

  //유저명 중복체크
  private void existsByUsername(String username) {
    boolean exist = userRepository.findAll().stream()
        .anyMatch(user -> user.getUsername().equals(username));
    if (exist) {
      throw new BusinessLogicException(ExceptionCode.DUPLICATE_USERNAME);
    }
  }

  //유저 이메일 중복체크
  private void existsByEmail(String email) {
    boolean exist = userRepository.findAll().stream()
        .anyMatch(user -> user.getEmail().equals(email));
    if (exist) {
      throw new BusinessLogicException(ExceptionCode.DUPLICATE_EMAIL);
    }
  }
}
