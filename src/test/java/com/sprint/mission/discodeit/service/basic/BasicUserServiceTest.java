package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.Role;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage storage;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @InjectMocks
    private BasicUserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("사용자 생성 성공")
    void create_success() throws IOException {
        // given
        UserCreateRequest request = new UserCreateRequest("달선", "dalsun@naver.com", "ekftjs123");
        UserDto userDto = new UserDto(UUID.randomUUID(), "달선", "dalsun@naver.com", null, true,
                Role.USER);
        BinaryContent savedProfile = new BinaryContent("image/png", 1024L, "달선.png");
        ReflectionTestUtils.setField(savedProfile, "id", UUID.randomUUID());

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);
        when(file.getOriginalFilename()).thenReturn("달선.png");
        when(file.getBytes()).thenReturn(new byte[1024]);

        when(userRepository.findByUsername("달선")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("dalsun@naver.com")).thenReturn(Optional.empty());
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);
        when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(savedProfile);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        // when
        UserDto result = userService.create(request, file);

        // then
        assertNotNull(result);
        assertEquals("달선", result.username());
        assertEquals("dalsun@naver.com", result.email());
        verify(binaryContentRepository).save(any());
        verify(storage).put(any(UUID.class), any(byte[].class));

    }

    @Test
    @DisplayName("사용자 생성 실패 - 이미 존재햐는 닉네임")
    void create_fail_already_exists_username() {
        // given
        UserCreateRequest request = new UserCreateRequest("달선", "dalsun@naver.com", "ekftjs123");
        User user = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);

        when(userRepository.findByUsername("달선")).thenReturn(Optional.of(user));

        // when, then
        assertThatThrownBy(() -> userService.create(request, null))
                .isInstanceOf(UserNameAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 업데이트 성공")
    void update_success() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("새로운 달선", "newDalsun@naver.com",
                "newnew123");
        User user = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);
        ReflectionTestUtils.setField(user, "id", id);

        UUID newProfileId = UUID.randomUUID(); // 새로운 프로필용 ID 생성
        BinaryContent savedProfile = new BinaryContent("image/png", 1024L, "new달선.png");
        ReflectionTestUtils.setField(savedProfile, "id", newProfileId);

        UserDto userDto = new UserDto(UUID.randomUUID(), "새로운 달선", "newDalsun@naver.com", null,
                true, Role.USER);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);
        when(file.getOriginalFilename()).thenReturn("new달선.png");
        when(file.getBytes()).thenReturn(new byte[1024]);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);
        when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(savedProfile);
        // when
        UserDto result = userService.update(id, request, file);

        // then
        assertNotNull(result);
        assertEquals("새로운 달선", result.username());
        assertEquals("newDalsun@naver.com", result.email());
        verify(binaryContentRepository).save(any());
        verify(storage).put(any(UUID.class), any(byte[].class));
    }

    @Test
    @DisplayName("업데이트 실패 - 이미 존재하는 이메일")
    void update_fail_already_exists_email() throws IOException {
        // given
        UUID id = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("새로운 달선", "newDalsun@naver.com",
                "newnew123");
        User user = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // when, then
        assertThatThrownBy(() -> userService.update(id, request, null))
                .isInstanceOf(EmailAlreadyExistException.class);

        verify(binaryContentRepository, never()).save(any());
        verify(storage, never()).put(any(UUID.class), any());
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void delete_success() {
        // given
        UUID id = UUID.randomUUID();
        User user = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // when
        userService.delete(id);

        // then
        verify(messageRepository).deleteByAuthorId(id);
        verify(readStatusRepository).deleteByUserId(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
    void delete_fail() {
        // given
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.delete(id))
                .isInstanceOf(UserNotFoundException.class);

        verify(messageRepository, never()).deleteByAuthorId(any());
        verify(readStatusRepository, never()).deleteByUserId(any());
        verify(userRepository, never()).deleteById(any());
    }
}
