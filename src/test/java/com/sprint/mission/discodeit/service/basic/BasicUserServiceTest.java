package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadException;
import com.sprint.mission.discodeit.exception.user.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicUserService userService;

  @Nested
  @DisplayName("유저 생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("성공: 모든 입력값이 올바르면 유저가 생성된다")
    void create_Success() {
      // given
      String username = "tester";
      given(userRepository.existsByUsername(username)).willReturn(false);
      given(userRepository.existsByEmail(anyString())).willReturn(false);

      User user = new User(username, "test@test.com", "pw", null);
      given(userRepository.save(any(User.class))).willReturn(user);

      // when
      User result = userService.create(username, "test@test.com", "pw", null);

      // then
      assertThat(result.getUsername()).isEqualTo(username);
      then(userRepository).should().save(any(User.class)); // 유저 저장 호출 확인
    }

    @Test
    @DisplayName("성공: 프로필 사진을 포함한 유저가 생성된다")
    void create_Success_WithProfileFile() throws IOException {
      // given
      String username = "tester";
      MockMultipartFile profileFile = new MockMultipartFile(
          "profileFile", "test.png", "image/png", "test-image".getBytes()); // 가짜 파일 생성

      given(userRepository.existsByUsername(username)).willReturn(false);
      given(userRepository.existsByEmail(anyString())).willReturn(false);

      // BinaryContent 저장 로직 모킹
      BinaryContent profile = new BinaryContent("test.png", 10L, "image/png");
      given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(profile);

      User user = new User(username, "test@test.com", "pw", profile);
      given(userRepository.save(any(User.class))).willReturn(user);

      // when
      User result = userService.create(username, "test@test.com", "pw", profileFile);

      // then
      assertThat(result.getProfile()).isNotNull();
      then(binaryContentStorage).should().put(any(UUID.class), any(byte[].class)); // 파일 저장 호출 확인
    }

    @Test
    @DisplayName("실패: 중복된 username이면 UserAlreadyExistsException이 발생한다")
    void create_Fail_DuplicateUsername() {
      // given
      String username = "duplicateUser";
      given(userRepository.existsByUsername(username)).willReturn(true);

      // when & then
      assertThrows(UserAlreadyExistsException.class, () -> {
        userService.create(username, "test@test.com", "pw", null);
      });
    }

    @Test
    @DisplayName("실패: 중복된 email이면 EmailAlreadyExistsException이 발생한다")
    void create_Fail_DuplicateEmail() {
      // given
      String email = "duplicateEmail";
      given(userRepository.existsByEmail(email)).willReturn(true);

      // when & then
      assertThrows(EmailAlreadyExistsException.class, () -> {
        userService.create("test", email, "pw", null);
      });
    }

    @Test
    @DisplayName("실패: 프로필 사진 설정 중 IOException이 발생하면 FileUploadException이 발생한다")
    void create_Fail_FileUploadError() throws IOException {
      // given
      String username = "tester";
      given(userRepository.existsByUsername(anyString())).willReturn(false);
      given(userRepository.existsByEmail(anyString())).willReturn(false);

      // mock 설정으로 가짜 MultipartFile 객체 생성
      MultipartFile mockProfileFile = org.mockito.Mockito.mock(MultipartFile.class);
      // 가짜 프로필 이미지 생성
      given(mockProfileFile.isEmpty()).willReturn(false);
      given(mockProfileFile.getOriginalFilename()).willReturn("test.png");
      given(mockProfileFile.getSize()).willReturn(10L);
      given(mockProfileFile.getContentType()).willReturn("image/png");

      // getBytes()가 호출될 때 IOException 발생하도록 설정
      given(mockProfileFile.getBytes()).willThrow(new IOException("Disk Full"));

      // when & then
      assertThrows(FileUploadException.class, () -> {
        userService.create("tester", "test@test.com", "pw", mockProfileFile);
      });
    }
  }

  @Nested
  @DisplayName("유저 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("성공: 새로운 이름으로 변경 시 중복이 없으면 정상 수정된다")
    void update_Success_NewUsername() {
      // given
      UUID userId = UUID.randomUUID();
      String newUsername = "newName";
      User existingUser = new User("oldName", "test@test.com", "pw", null);

      given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
      given(userRepository.existsByUsername(newUsername)).willReturn(false);

      // when
      User result = userService.update(userId, newUsername, null, null, null);

      // then
      assertThat(result.getUsername()).isEqualTo(newUsername);
      then(userRepository).should().findById(userId); // 조회가 정상적으로 일어났는지 확인
    }

    @Test
    @DisplayName("실패: 다른 사람이 쓰고 있는 username으로 변경 시 UserAlreadyExistsException이 발생한다")
    void update_Fail_DuplicateUsername() {
      // given
      UUID userId = UUID.randomUUID();
      String duplicateName = "alreadyInUse";
      User existingUser = new User("myName", "test@test.com", "pw", null);

      given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
      given(userRepository.existsByUsername(duplicateName)).willReturn(true);

      // when & then
      assertThrows(UserAlreadyExistsException.class, () -> {
        userService.update(userId, duplicateName, null, null, null);
      });
    }

    @Test
    @DisplayName("실패: 다른 사람이 쓰고 있는 email으로 변경 시 EmailAlreadyExistsException이 발생한다")
    void update_Fail_DuplicateEmail() {
      // given
      UUID userId = UUID.randomUUID();
      String duplicateEmail = "alreadyInUse@test.com";
      User existingUser = new User("myName", "test@test.com", "pw", null);

      given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
      given(userRepository.existsByEmail(duplicateEmail)).willReturn(true);

      // when & then
      assertThrows(EmailAlreadyExistsException.class, () -> {
        userService.update(userId, null, duplicateEmail, null, null);
      });
    }

    @Test
    @DisplayName("실패: 프로필 사진 수정 중 IOException이 발생하면 FileUploadException이 발생한다")
    void update_Fail_FileUploadError() throws IOException {
      // given
      UUID userId = UUID.randomUUID();
      User existingUser = new User("tester", "test@test.com", "pw", null);

      given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

      // mock 설정으로 가짜 MultipartFile 객체 생성
      MultipartFile mockProfileFile = org.mockito.Mockito.mock(MultipartFile.class);
      // 가짜 프로필 이미지 생성
      given(mockProfileFile.isEmpty()).willReturn(false);
      given(mockProfileFile.getOriginalFilename()).willReturn("new-test.png");
      given(mockProfileFile.getSize()).willReturn(10L);
      given(mockProfileFile.getContentType()).willReturn("image/png");

      // getBytes()가 호출될 때 IOException 발생하도록 설정
      given(mockProfileFile.getBytes()).willThrow(new IOException("Storage Full"));

      // when & then
      assertThrows(FileUploadException.class, () -> {
        userService.update(userId, null, null, null, mockProfileFile);
      });
    }

    @Test
    @DisplayName("실패: 존재하지 않는 유저 수정 시 UserNotFoundException이 발생한다")
    void update_Fail_NotFound() {
      // given
      UUID userId = UUID.randomUUID();
      given(userRepository.findById(userId)).willReturn(Optional.empty());

      // when & then
      assertThrows(UserNotFoundException.class, () -> {
        userService.update(userId, "anyName", null, null, null);
      });
    }
  }

  @Nested
  @DisplayName("유저 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("성공: 존재하는 유저 ID로 삭제 요청 시 정상적으로 삭제된다")
    void delete_Success() {
      // given
      UUID userId = UUID.randomUUID();
      User existingUser = new User("tester", "test@test.com", "pw", null);

      // 삭제 전 유저가 존재하는지 먼저 확인하므로 mock 설정
      given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

      // when
      userService.deleteById(userId);

      // then
      then(userRepository).should().delete(existingUser); // delete가 실제로 호출되었는지 확인
    }

    @Test
    @DisplayName("실패: 존재하지 않는 유저 ID로 삭제 요청 시 UserNotFoundException이 발생한다")
    void delete_Fail_NotFound() {
      // given
      UUID userId = UUID.randomUUID();

      given(userRepository.findById(userId)).willReturn(Optional.empty()); // 유저가 없다고 가정

      // when & then
      assertThrows(UserNotFoundException.class, () -> {
        userService.deleteById(userId);
      });

      then(userRepository).should(never())
          .delete(any(User.class)); // 삭제 로직까지 가지 않고 예외가 터졌는지 확인 (delete는 호출되지 않아야 함)
    }
  }
}