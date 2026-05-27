package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.AlreadyExistsEmailException;
import com.sprint.mission.discodeit.exception.user.AlreadyExistsNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {
    @InjectMocks
    private BasicUserService basicUserService;

    @Mock private UserStatusRepository userStatusRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @Test
    @DisplayName("유저 생성 성공 - 정상적인 요청시 UserDTO 반환")
    void 유저_생성_성공_테스트() {
        // given
        UserCreateRequest createRequest = new UserCreateRequest("전승주", "jsj@naver.com", "12345678");
        User user = new User("전승주", "jsj@naver.com", "12345678", null);
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), null, false);
        UserStatus userStatus = new UserStatus(user);

        // 영향 안받도록
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDTO(any(User.class))).willReturn(userDTO);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);

        // when - 실제 실행
        UserDTO result = basicUserService.create(createRequest, Optional.empty());

        // then

        // 응답 Dto, result가 동일한지
        assertEquals(userDTO, result);
        // 저장 검증
        then(userRepository).should().existsByUsername("전승주");
        then(userRepository).should().existsByEmail("jsj@naver.com");

        then(userStatusRepository).should().save(any(UserStatus.class));
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("유저 생성 실패 - 중복 이름 - AlreadyExistsName 예외 반환")
    void 유저_생성_실패_테스트() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("전승주", "test@test", "12345678");
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        // when, then
       assertThrows(AlreadyExistsNameException.class, () -> basicUserService.create(userCreateRequest, Optional.empty()));
    }

    @Test
    @DisplayName("유저 수정 성공 - 정상적인 요청 시 수정된 UserDTO 반환")
    void 유저_수정_성공_테스트() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("주승전", "jsj@gmail.com", "12345678");
        UserDTO userDTO = new UserDTO(userId, userUpdateRequest.newUsername(), userUpdateRequest.newEmail(), null, false);


        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(new User("전승주2", "jsj2@naver.com", "12345678", null)));
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userMapper.toDTO(any(User.class))).willReturn(userDTO);

        // when
        UserDTO result = basicUserService.update(userId, userUpdateRequest, Optional.empty());

        // then
        assertEquals(result, userDTO);

        then(userRepository).should().existsByUsername(anyString());
        then(userRepository).should().existsByEmail(anyString());
    }

    @Test
    @DisplayName("유저 수정 실패 - 중복 이메일 - AlreadyExistsEmailException 반환")
    void 유저_수정_실패_테스트() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("승주전", "sjj@naver.com", "12345678");

        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(mock(User.class)));
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when, then
        assertThrows(AlreadyExistsEmailException.class, () -> basicUserService.update(userId, userUpdateRequest, Optional.empty()));

        then(userRepository).should().existsByUsername(anyString());
        then(userRepository).should().existsByEmail(anyString());
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("유저 삭제 성공 - 정상적인 요청 시 삭제 성공")
    void 유저_삭제_성공_테스트() {
        // given
        User mockUser = mock(User.class);
        UUID userId = UUID.randomUUID();

        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(mockUser));

        // when
        basicUserService.deleteUser(userId);

        // then
        then(userRepository).should().delete(any());
    }

    @Test
    @DisplayName("유저 삭제 실패 - 존재하지 않는 유저")
    void 유저_삭제_실패_테스트() {
        // given
        UUID userId = UUID.randomUUID();

        given(userRepository.findById(any(UUID.class))).willThrow(new UserNotFoundException(userId));

        // when, then
        assertThrows(UserNotFoundException.class, () -> basicUserService.deleteUser(userId));
        then(userRepository).should(never()).deleteById(any(UUID.class));
    }
}