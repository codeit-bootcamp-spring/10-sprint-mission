package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatusRepository userStatusRepository;

    @Mock
    private BinaryContentService binaryContentService;

    @InjectMocks
    private BasicUserService userService;

    // CREATE
    @DisplayName("유저와 유저상태를 생성한다")
    @Test
    void create_succes_saves_user_and_status(){
        //given 회원가입 요청 데이터 생성
        UserCreateRequest request = new UserCreateRequest(
                "tester",
                "tester@test.com",
                "1234"
        );
        when(userRepository.existsByEmail("tester@test.com"))
                .thenReturn(false);
        when(userRepository.existsByUsername("tester"))
                .thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userStatusRepository.save(any(UserStatus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        User savedUser = userService.create(request, Optional.empty());

        // then
        // 결과값 검증
        assertEquals("tester", savedUser.getUsername());
        assertEquals("tester@test.com", savedUser.getEmail());
        assertEquals("1234", savedUser.getPassword());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userCaptor.capture());
        // save 된 객체 내부 검증
        assertEquals("tester", userCaptor.getValue().getUsername());
        assertEquals("tester@test.com", userCaptor.getValue().getEmail());
        assertEquals("1234", userCaptor.getValue().getPassword());
    }

    @Test
    void create_fail_when_email_exists() {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "tester",
                "tester@test.com",
                "1234"
        );
        // repository Mock -> true로 고정
        given(userRepository.existsByEmail("tester@test.com")).willReturn(true);

        // when & then
        assertThrows(UserAlreadyExistsException.class,
                () -> userService.create(request, Optional.empty()));

        then(userRepository).should().existsByEmail("tester@test.com");
        then(userRepository).shouldHaveNoMoreInteractions();
        then(userStatusRepository).shouldHaveNoInteractions();
    }

    // UPDATE
    @Test
    void update_success() {
        // given
        UUID userId = UUID.randomUUID();

        User user = new User(
                "oldName",
                "old@test.com",
                "1111",
                null
        );

        UserUpdateRequest request = new UserUpdateRequest(
                "newName",
                "new@test.com",
                "2222"
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("new@test.com")).willReturn(false);
        given(userRepository.existsByUsername("newName")).willReturn(false);

        // when
        User updatedUser = userService.update(userId, request, Optional.empty());

        // then
        assertEquals("newName", updatedUser.getUsername());
        assertEquals("new@test.com", updatedUser.getEmail());
        assertEquals("2222", updatedUser.getPassword());
        // repo 저장 확인용
        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail("new@test.com");
        then(userRepository).should().existsByUsername("newName");
    }



    @Test
    void update_fail_when_user_not_found() {
        // given
        UUID userId = UUID.randomUUID();

        UserUpdateRequest request = new UserUpdateRequest(
                "newName",
                "new@test.com",
                "2222"
        );
        //not found
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.update(userId, request, Optional.empty()));

        then(userRepository).should().findById(userId);
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    // DELETE
    @Test
    void delete_success() {
        // given
        UUID userId = UUID.randomUUID();

        User user = new User(
                "tester",
                "tester@test.com",
                "1234",
                null
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        userService.delete(userId);

        // then
        then(userRepository).should().findById(userId);
        then(userRepository).should().delete(user);
    }

    @Test
    void delete_fail_when_user_not_found() {
        // given
        UUID userId = UUID.randomUUID();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> userService.delete(userId));

        then(userRepository).should().findById(userId);
        then(userRepository).shouldHaveNoMoreInteractions();
    }

}

