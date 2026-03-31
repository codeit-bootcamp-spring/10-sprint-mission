package com.sprint.mission.discodeit.user;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContent binaryContent;

    @InjectMocks
    private UserService userService;

    /*
    userCreate 테스트
     */
    @Test
    void userCreatePassTest() {
        // given
        User user = new User("김통과", "pass@google.com", "12345", binaryContent);
        UserCreateRequest request = new UserCreateRequest(
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );

        // when
        userService.create(request, null);

        // then
        verify(userService, times(1))
                .create(request, null);
    }

    @Test
    void userCreateFailTest() {
        // given
        User user = new User("김실패", "", "", binaryContent);
        UserCreateRequest request = new UserCreateRequest(
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );

        // when
        userService.create(request, null);

        // then
        verify(userService, times(1))
                .create(request, null);
    }

    /*
    userUpdate 테스트
     */
    @Test
    void userUpdatePassTest() {
        // given
        User user = new User("김통과", "pass@google.com", "12345", binaryContent);
        UserUpdateRequest request = new UserUpdateRequest(
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );

        // when
        userService.update(user.getId(), request, null);

        // then
        verify(userService, times(1))
                .update(user.getId(), request, null);
    }

    @Test
    void userUpdateFailTest() {
        // given
        User user = new User("김실패", "fail@google.com", "", binaryContent);
        UserUpdateRequest request = new UserUpdateRequest(
                user.getUsername(),
                user.getEmail(),
                user.getPassword()
        );

        // when
        userService.update(user.getId(), request, null);

        // then
        verify(userService, times(1))
                .update(user.getId(), request, null);
    }

    /*
    userDelete 테스트
     */
    @Test
    void userDeletePassTest() {
        // given
        User user = new User("김통과", "pass@google.com", "12345", binaryContent);
        userRepository.save(user);

        // when
        userService.delete(user.getId());

        // then
        verify(userService, times(1))
                .delete(any());;
    }

    @Test
    void userDeleteFailTest() {
        // given
        User user = new User("김실패", "fail@google.com", "", binaryContent);

        // when
        userService.delete(user.getId());

        // then
        verify(userService, times(1))
                .delete(any());;
    }
}
