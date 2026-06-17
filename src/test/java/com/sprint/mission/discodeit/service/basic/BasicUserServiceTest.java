package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserRequestDto;
import com.sprint.mission.discodeit.dto.UpdateUserRequestDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {
    // BasicUserService가 의존하는 레포지토리를 명시적으로 선언
    @Mock
    private BinaryContentRepository binaryContentRepository;

    // BasicUserService가 의존하는 레포지토리를 명시적으로 선언
    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private UserRepository userRepository;


    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService basicUserService;

    // ==========================================
    // Create Test
    // ==========================================
    @Test
    @DisplayName("유효한 데이터로 사용자를 생성할 수 있어야 합니다.")
    void create_success() {
        // given
        CreateUserRequestDto request = new CreateUserRequestDto("tester", "test@test.com", "password123", null);
        User user = new User(request.username(), request.email(), request.password());
        UserDto expectedDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, Role.USER, true);

        // 중복 검사
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
        given(userRepository.existsByEmail(anyString())).willReturn(false);

        given(userMapper.toDto(any(User.class), anyBoolean())).willReturn(expectedDto);

        // when
        UserDto result = basicUserService.create(request);

        // then
        assertEquals(expectedDto.username(), result.username());
        assertEquals(expectedDto.email(), result.email());

        verify(userRepository).save(any(User.class));
        verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("이미 존재하는 username으로 가입 시도 시 예외가 발생해야 합니다.")
    void create_fail_duplicate_username() {
        // given
        CreateUserRequestDto request = new CreateUserRequestDto("duplicateUser", "test@test.com", "password123", null);
        User existingUser = new User("duplicateUser", "old@test.com", "oldPassword");

        given(userRepository.findByUsername(request.username())).willReturn(Optional.of(existingUser));

        // when, then
        assertThrows(UserAlreadyExistsException.class, () -> basicUserService.create(request));
    }

    @Test
    @DisplayName("이미 존재하는 email로 가입 시도 시 예외가 발생해야 합니다.")
    void create_fail_duplicate_email() {
        // given
        CreateUserRequestDto request = new CreateUserRequestDto("newUser", "duplicate@test.com", "password123", null);

        given(userRepository.findByUsername(request.username())).willReturn(Optional.empty());
        given(userRepository.existsByEmail(request.email())).willReturn(true);

        // when, then
        assertThrows(UserAlreadyExistsException.class, () -> basicUserService.create(request));
    }

    // ==========================================
    // Update Test
    // ==========================================
    @Test
    @DisplayName("존재하는 사용자의 정보를 성공적으로 수정할 수 있어야 합니다.")
    void update_success() {
        // given
        UUID userId = UUID.randomUUID();
        UpdateUserRequestDto request = new UpdateUserRequestDto("newTester", "new@test.com", "newPass123", null);

        User user = new User("oldTester", "old@test.com", "oldPass");
        UserDto expectedDto = new UserDto(user.getId(), request.newUsername(), request.newEmail(), null, Role.USER, true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        given(userMapper.toDto(eq(user), anyBoolean())).willReturn(expectedDto);

        // when
        UserDto result = basicUserService.update(userId, request);

        // then
        assertEquals("newTester", user.getUsername()); // 엔티티 자체가 수정되었는지 검증
        assertEquals("new@test.com", user.getEmail());
        assertEquals(expectedDto.username(), result.username());
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 정보를 수정하려고 하면 예외가 발생해야 합니다.")
    void update_fail_userNotFound() {
        // given
        UUID notFoundUserId = UUID.randomUUID();
        UpdateUserRequestDto request = new UpdateUserRequestDto("newTester", "new@test.com", "newPass123", null);

        given(userRepository.findById(notFoundUserId)).willReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class, () -> basicUserService.update(notFoundUserId, request));
    }

    // ==========================================
    // Delete Test
    // ==========================================
    @Test
    @DisplayName("존재하는 사용자를 성공적으로 삭제할 수 있어야 합니다.")
    void delete_success() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("tester", "test@test.com", "password");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        basicUserService.delete(userId);

        // then
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 삭제하려고 하면 예외가 발생해야 합니다.")
    void delete_fail_userNotFound() {
        // given
        UUID notFoundUserId = UUID.randomUUID();

        given(userRepository.findById(notFoundUserId)).willReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class, () -> basicUserService.delete(notFoundUserId));
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 조회하면 UserNotFoundException 예외가 발생한다.")
    void find_user_not_found_exception() {
        // given
        UUID fakeId = UUID.randomUUID();
        when(userRepository.findById(fakeId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> basicUserService.find(fakeId));
    }
}