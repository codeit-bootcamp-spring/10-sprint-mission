package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentUploadException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
        log.debug("프로필 사진 업로드 시작: name={}, size={}", file.getOriginalFilename(), file.getSize());
        profile = new BinaryContent(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType()
        );
        binaryContentRepository.save(profile);
        binaryContentStorage.put(profile.getId(), file.getBytes());
        log.info("프로필 사진 저장 성공: profileId={}", profile.getId());
      } catch (IOException e) {
        throw new BinaryContentUploadException(e);
      }
    }

    User user = userMapper.toEntity(request, profile);
    UserStatus userStatus = new UserStatus(user);

    user.setStatus(userStatus); // 편의 메서드
    userRepository.save(user); //cascade로 UserStatus도 같이 INSERT
    log.info("유저 생성 완료: userId={}", user.getId());
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findById(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    log.debug("유저 조회 완료: user={}, username={}", user.getId(), user.getUsername());
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll();
    log.debug("유저 목록 조회 완료: userCount={}", users.size());
    return users.stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest request, MultipartFile file) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    validateUpdate(user, request);
    user.update(request);

    if (file != null && !file.isEmpty()) { //요청에 프로필 파일이 있는지 확인
      try {
        log.debug("새로운 프로필 사진 업로드 시작: name={}, size={}", file.getOriginalFilename(),
            file.getSize());
        BinaryContent newProfile = new BinaryContent(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType()
        );
        binaryContentRepository.save(newProfile);
        binaryContentStorage.put(newProfile.getId(), file.getBytes());
        user.updateProfile(newProfile);
        log.info("새로운 프로필 사진 저장 성공: profileId={}", newProfile.getId());
      } catch (IOException e) {
        throw new BinaryContentUploadException(e);
      }
    }
    log.info("유저 수정 완료: userId={}", user.getId());
    return userMapper.toDto(user);
  }

  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    userRepository.delete(user);
    log.info("유저 삭제 완료: userId={}", userId);
  }

  //유저명 중복체크
  private void existsByUsername(String username) {
    boolean exist = userRepository.existsByUsername(username);
    if (exist) {
      throw new DuplicateUsernameException(Map.of("username", username));
    }
  }

  //유저 이메일 중복체크
  private void existsByEmail(String email) {
    boolean exist = userRepository.existsByEmail(email);
    if (exist) {
      throw new DuplicateEmailException(Map.of("email", email));
    }
  }

  private void validateUpdate(User user, UserUpdateRequest request) {
    // 유저네임이 변경되었다면 중복 체크
    if (request.newUsername() != null && !request.newUsername().equals(user.getUsername())) {
      existsByUsername(request.newUsername());
    }
    // 이메일이 변경되었다면 중복 체크
    if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())) {
      existsByEmail(request.newEmail());
    }
  }
}
