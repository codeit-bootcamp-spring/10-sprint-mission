package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public UserDto create(UserCreateRequest request, MultipartFile file) {
    existsByUsername(request.username());
    existsByEmail(request.email());

    //프로필 설정하지 않으면 null
    BinaryContent profile = null;

    //요청에 프로필이 있다면 binaryContent 객체 생성 후 저장
    if (file != null && !file.isEmpty()) {
      try {
        profile = new BinaryContent(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType()
        );
        binaryContentRepository.save(profile);
        binaryContentStorage.put(profile.getId(), file.getBytes());
      } catch (IOException e) {
        throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_UPLOAD_FAILED);
      }
    }

    User user = new User(
        request.username(),
        request.email(),
        request.password(),
        profile
    );

    UserStatus userStatus = new UserStatus(user);

    user.setStatus(userStatus); // 편의 메서드
    userRepository.save(user); //cascade로 UserStatus도 같이 INSERT

    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findById(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest request, MultipartFile file) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    if (request.newUsername() != null && !request.newUsername().equals(user.getUsername())) {
      existsByUsername(request.newUsername());
      user.updateUsername(request.newUsername());
    }
    if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())) {
      existsByEmail(request.newEmail());
      user.updateEmail(request.newEmail());
    }
    Optional.ofNullable(request.newPassword()).ifPresent(user::updatePassword);

    if (file != null && !file.isEmpty()) { //요청에 프로필 파일이 있는지 확인
      try {
        BinaryContent newProfile = new BinaryContent(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType()
        );
        binaryContentRepository.save(newProfile);
        binaryContentStorage.put(newProfile.getId(), file.getBytes());
        user.updateProfile(newProfile);
      } catch (IOException e) {
        throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_UPLOAD_FAILED);
      }

    }

    return userMapper.toDto(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    userRepository.delete(user);
  }

  //유저명 중복체크
  private void existsByUsername(String username) {
    boolean exist = userRepository.existsByUsername(username);
    if (exist) {
      throw new BusinessLogicException(ExceptionCode.DUPLICATE_USERNAME);
    }
  }

  //유저 이메일 중복체크
  private void existsByEmail(String email) {
    boolean exist = userRepository.existsByEmail(email);
    if (exist) {
      throw new BusinessLogicException(ExceptionCode.DUPLICATE_EMAIL);
    }
  }
}
