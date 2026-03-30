package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("회원 가입을 진행할 수 있어야 합니다. ")
    void should_user_create() {

        // given 준비
        UserCreateRequest user = new UserCreateRequest("곽인성","kis2690@naver.com","1234");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(new UserDto(UUID.randomUUID(),"곽인성","kis2690@naver.com",null,true));

        // when 실행
        UserDto createdUser = userService.create(user,Optional.empty());

        // then
        assertNotNull(createdUser);
        assertEquals(user.username(),createdUser.username());
        assertEquals(user.email(),createdUser.email());

    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입시 회원가입에 실패해야됩니다.")
    void exist_email_fail_user_create(){
        // given
        UserCreateRequest user = new UserCreateRequest("곽인성","kis2690@naver.com","1234");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when,then
        assertThrows(EmailAlreadyExistException.class,() -> userService.create(user,Optional.empty()) );
        verify(userRepository, never()).save(any(User.class));//userRepository.save()가 실행되지 않았다.(0번 호출되어야한다.)
    }

    @Test
    @DisplayName("프로필 없는 회원 수정 성공")
    void update_username() {
        // given
        User mockUser = mock(User.class); //원래 있었던 User라고 가정.
        UUID userId = UUID.randomUUID();
        UserUpdateRequest updateRequest = new UserUpdateRequest("new곽인성","new@naver.com", "12345");
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockUser));
        when(mockUser.getEmail()).thenReturn("kis2690@naver.com");
        when(mockUser.getUsername()).thenReturn("곽인성");
        when(userRepository.existsByEmail("new@naver.com")).thenReturn(false);
        when(userRepository.existsByUsername("new곽인성")).thenReturn(false);
        when(userMapper.toDto(mockUser)).thenReturn(mock(UserDto.class));

        //when
        UserDto updatedUser = userService.update(userId, updateRequest, Optional.empty());

        // then
        verify(mockUser).update("new곽인성", "new@naver.com", "12345", null);
        verify(binaryContentRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 회원일시 수정에 실패해야한다.")
    void update_fail_when_not_exist_user() {
        // given
        User mockUser = mock(User.class);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        // when,then
        assertThrows(UserNotFoundException.class,() -> userService.update(UUID.randomUUID(),null,Optional.empty()));
        verify(mockUser, never()).update(anyString(),anyString(),anyString(),any());
        verify(userRepository, never()).save(any(User.class));
        verify(binaryContentRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원 삭제를 진행할 수 있어야한다.")
    void delete_user() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(any(UUID.class))).thenReturn(true);

        // when
        userService.delete(userId);

        // then
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("삭제하려는 userId가 없을때 회원 삭제에 실패해야합니다. ")
    void delete_fail_when_user_not_found() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(any(UUID.class))).thenReturn(false);

        // when, then
        assertThrows(UserNotFoundException.class,() -> userService.delete(userId));
        verify(userRepository,never()).deleteById(userId);
    }
}