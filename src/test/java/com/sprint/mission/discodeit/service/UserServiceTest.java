package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;


    // 0. 공통 메소드
    private User makeUser(String username, String email) {
        User user = new User(username, email, "password", null);
        return user;
    }

    private UserDto makeUserDto(User user) {
        return new UserDto(UUID.randomUUID(), user.getUsername(), user.getEmail(), null, true);
    }

    // === 1. create ===
    // 1-1. [성공] 프로필이 없는 유저 생성
    @Test
    void create_success_noProfile() {
        // given
        UserCreateRequest request = new UserCreateRequest("lynn", "lynn@test.com", "lynn1234");
        User savedUser = makeUser("lynn", "lynn@test.com");
        UserDto userDto = makeUserDto(savedUser);

        // when
        UserDto result = userService.create(request, Optional.empty());

        // then
        assertThat(result.username()).isEqualTo("lynn");
        assertThat(result.email()).isEqualTo("lynn@test.com");

        then(userRepository).should().save(any(User.class));
        then(binaryContentRepository).should(never()).save(any());
        then(binaryContentStorage).should(never()).put(any(), any());
    }

    // 1-2. [실패] 이미 존재하는 이메일로 유저 생성할 때 UserAlreadyExistsException 발생
    @Test
    void create_fail_duplicateEmail() {
        // given
        UserCreateRequest request = new UserCreateRequest("hyun", "dup@test.com", "hyun1234");
        given(userRepository.existsByEmail("dup@test.com")).willReturn(true);

        // when
        assertThatThrownBy(() -> userService.create(request, Optional.empty()))
                .isInstanceOf(UserAlreadyExistException.class);

        // then
        then(userRepository).should(never()).save(any());
    }

    // === 2. update ===
    // 2-1. [성공] 유효한 요청으로 유저 정보 수정
    @Test
    void update_success_updateUsernameAndEmail() {
        // given
        UUID userId = UUID.randomUUID();
        User user = makeUser("oldName", "old@test.com");
        UserUpdateRequest userUpdateRequest =
                new UserUpdateRequest("newName", "new@test.com", "new1234");
        UserDto expectedDto = new UserDto(userId, "newName", "new@test.com", null, true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("new@test.com")).willReturn(false);
        given(userRepository.existsByUsername("newName")).willReturn(false);
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = userService.update(userId, userUpdateRequest, Optional.empty());

        // then
        assertThat(result.username()).isEqualTo("newName");
        assertThat(result.email()).isEqualTo("new@test.com");

    }

    // 2-2. [실패] 존재하지 않는 userId로 수정할 때 UserNotFoundException 발생
    @Test
    void update_fail_userNotFound() {
        // given
            UUID unknownId = UUID.randomUUID();
            UserUpdateRequest userUpdateRequest =
                    new UserUpdateRequest("unknown", "unknown@test.com", "unknown1234");

            given(userRepository.findById(unknownId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.update(unknownId, userUpdateRequest, Optional.empty()))
                .isInstanceOf(UserNotFoundException.class);
    }

    // === 3. delete ===
    // 3-1. [성공] 존재하는 유저 삭제
    @Test
    void delete_success() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.existsById(userId)).willReturn(true);

        // when
        userService.delete(userId);

        // then
        then(userRepository).should().deleteById(userId);
    }

    // 3-2. [실패] 존재하는 않는 userId로 삭제할 때 UserNotFoundException 발생
    @Test
    void delete_fail() {
        // given
        UUID unknownId = UUID.randomUUID();
        given(userRepository.existsById(unknownId)).willReturn(false);

        // when
        assertThatThrownBy(() -> userService.delete(unknownId))
                .isInstanceOf(UserNotFoundException.class);

        // then
        then(userRepository).should(never()).deleteById(any());
    }
}
