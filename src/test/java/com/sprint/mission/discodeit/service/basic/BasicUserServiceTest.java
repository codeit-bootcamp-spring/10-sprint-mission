package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
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
    void update() {
    }

    @Test
    void delete() {
    }
}