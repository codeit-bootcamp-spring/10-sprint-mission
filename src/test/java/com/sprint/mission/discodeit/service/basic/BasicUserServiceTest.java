package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.global.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BinaryContentMapper binaryContentMapper;

    @InjectMocks
    private BasicUserService basicUserService;

    @Test
    @DisplayName("프로필 사진 없이 유저 생성을 할 수 있어야 합니다.")
    void create_user_by_no_profile() {
        // given
        CreateUserRequestDTO request =
                new CreateUserRequestDTO("test@test.com", "test", "1234");

        User savedUser = new User("test", "test@test.com", "1234", null);
        UserDto response = new UserDto(null, "test", "test@test.com", null, null);

        given(userRepository.existsByUsername("test")).willReturn(false);
        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(userRepository.saveAndFlush(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(response);

        // when
        UserDto result = basicUserService.createUser(request, null);

        // then
        assertThat(result).isEqualTo(response);

        then(userRepository).should().existsByUsername("test");
        then(userRepository).should().existsByEmail("test@test.com");
        then(userRepository).should().saveAndFlush(any(User.class));
        then(userMapper).should().toDto(savedUser);

        // 프로필 이미지가 없으므로 호출되면 안 됨
        then(binaryContentMapper).shouldHaveNoInteractions();
        then(binaryContentStorage).should(never()).put(any(), any());
    }

    @Test
    @DisplayName("프로필 사진이 있을 때도 유저 생성을 할 수 있어야 합니다.")
    void create_user_by_profile_exists() {
        // given -> 객체 생성할 때는 any를 사용하면 안됨
        CreateUserRequestDTO request =
                new CreateUserRequestDTO("test@test.com", "test", "1234");

        byte[] bytes = "test-image".getBytes();
        long size = bytes.length;

        CreateBinaryContentPayloadDTO profile =
                new CreateBinaryContentPayloadDTO(bytes, "jpg", "test", size);

        BinaryContent bc = new BinaryContent("jpg", "test", size);
        User savedUser = new User("test", "test@test.com", "1234", bc);

        BinaryContentDto profileDto = new BinaryContentDto(null, "test", size, "jpg");
        UserDto response = new UserDto(null, "test", "test@test.com", profileDto, null);

        given(userRepository.existsByUsername("test")).willReturn(false);
        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(binaryContentMapper.toEntity(profile)).willReturn(bc);
        given(userRepository.saveAndFlush(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(savedUser)).willReturn(response);

        // when
        UserDto result = basicUserService.createUser(request, profile);

        // then
        assertThat(result).isEqualTo(response);

        then(userRepository).should().existsByUsername("test");
        then(userRepository).should().existsByEmail("test@test.com");
        then(userRepository).should().saveAndFlush(any(User.class));
        then(userMapper).should().toDto(savedUser);

        then(binaryContentMapper).should().toEntity(profile);
        then(binaryContentStorage).should().put(any(), eq(profile.bytes()));
    }

    @Test
    @DisplayName("중복되는 이름이 있으면 예외를 던집니다.")
    void if_username_already_exists_throw_exception() {
        // given
        CreateUserRequestDTO request =
                new CreateUserRequestDTO("test@test.com", "test", "1234");

        given(userRepository.existsByUsername("test")).willReturn(true);

        // when, then
        assertThrows(DuplicateResourceException.class,
                () -> basicUserService.createUser(request, null));

        then(userRepository).should().existsByUsername("test");
        then(userRepository).should(never()).existsByEmail(any());
        then(userRepository).should(never()).saveAndFlush(any());
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("중복되는 이메일이 있으면 예외를 던집니다.")
    void if_email_already_exists_throw_exception() {
        // given
        CreateUserRequestDTO request =
                new CreateUserRequestDTO("test@test.com", "test", "1234");

        given(userRepository.existsByUsername("test")).willReturn(false);
        given(userRepository.existsByEmail("test@test.com")).willReturn(true);

        // when, then
        assertThrows(DuplicateResourceException.class,
                () -> basicUserService.createUser(request, null));

        then(userRepository).should().existsByEmail("test@test.com");
        // 서비스 코드 구조상 username 검증은 email 검증보다 선행되기 때문에 무조건 username 검증은 수행됨
        then(userRepository).should(never()).saveAndFlush(any());
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("username을 수정할 수 있습니다.")
    void update_username() {
        // given
        UUID userId = UUID.randomUUID();
        UpdateUserRequestDTO request =
                new UpdateUserRequestDTO("update", null, null);

        // 기존 유저
        User user = new User("test", "test@test.com", "1234", null);
        // 결과 DTO
        UserDto response =
                new UserDto(null, "update", "test@test.com", null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByUsername("update")).willReturn(false);
        given(userMapper.toDto(user)).willReturn(response);

        // when
        UserDto result = basicUserService.updateUserInfo(userId, request, null);

        // then
        assertThat(result).isEqualTo(response);

        // 핵심 검증
        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByUsername("update");

        // 실제로 값이 바뀌었는지 확인
        assertThat(user.getUsername()).isEqualTo("update");

        // 프로필 관련 로직 안 타야 함
        then(binaryContentMapper).shouldHaveNoInteractions();
        then(binaryContentStorage).should(never()).put(any(), any());
    }

    @Test
    @DisplayName("email을 수정할 수 있습니다.")
    void update_email() {
        // given
        UUID userId = UUID.randomUUID();
        UpdateUserRequestDTO request =
                new UpdateUserRequestDTO(null, "update@test.com", null);

        // 기존 유저
        User user = new User("test", "test@test.com", "1234", null);
        // 결과 DTO
        UserDto response =
                new UserDto(null, "test", "update@test.com", null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("update@test.com")).willReturn(false);
        given(userMapper.toDto(user)).willReturn(response);

        // when
        UserDto result = basicUserService.updateUserInfo(userId, request, null);

        // then
        assertThat(result).isEqualTo(response);

        // 핵심 검증
        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail("update@test.com");

        // 실제로 값이 바뀌었는지 확인
        assertThat(user.getEmail()).isEqualTo("update@test.com");

        // 프로필 관련 로직 안 타야 함
        then(binaryContentMapper).shouldHaveNoInteractions();
        then(binaryContentStorage).should(never()).put(any(), any());
    }

    @Test
    @DisplayName("프로필 사진을 수정할 수 있습니다.")
    void update_profile() {
        // given
        UUID userId = UUID.randomUUID();
        byte[] bytes = "test-image".getBytes();
        long size = bytes.length;

        UpdateUserRequestDTO request =
                new UpdateUserRequestDTO(null, null, null);

        CreateBinaryContentPayloadDTO profile =
                new CreateBinaryContentPayloadDTO(bytes, "jpg", "test", size);

        BinaryContent bc = new BinaryContent("jpg", "test", size);
        BinaryContent savedProfile = bc; // 단순화: save 결과도 같은 객체로 가정

        BinaryContentDto profileDto = new BinaryContentDto(null, "test", size, "jpg");

        // 기존 유저
        User user = new User("test", "test@test.com", "1234", null);

        // 결과 DTO
        UserDto response =
                new UserDto(null, "test", "test@test.com", profileDto, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(binaryContentMapper.toEntity(profile)).willReturn(bc);
        given(binaryContentRepository.save(bc)).willReturn(savedProfile);
        given(userRepository.saveAndFlush(user)).willReturn(user);
        given(userMapper.toDto(user)).willReturn(response);

        // when
        UserDto result = basicUserService.updateUserInfo(userId, request, profile);

        // then
        assertThat(result).isEqualTo(response);

        // 핵심 검증
        then(userRepository).should().findById(userId);
        then(binaryContentMapper).should().toEntity(profile);
        then(binaryContentRepository).should().save(bc);
        then(userRepository).should().saveAndFlush(user);
        then(binaryContentStorage).should().put(any(), eq(profile.bytes()));
        then(userMapper).should().toDto(user);

        // 실제로 값이 바뀌었는지 확인
        assertThat(user.getProfile()).isEqualTo(savedProfile);

        // username / email 관련 로직은 안 타야 함
        then(userRepository).should(never()).existsByUsername(any());
        then(userRepository).should(never()).existsByEmail(any());
    }

    @Test
    @DisplayName("중복된 username으로 수정하면 예외를 던집니다.")
    void if_new_username_is_already_exists_throw_exception() {
        // given
        UUID userId = UUID.randomUUID();
        UpdateUserRequestDTO request =
                new UpdateUserRequestDTO("update", null, null);

        // 기존 유저
        User user = new User("test", "test@test.com", "1234", null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByUsername("update")).willReturn(true);

        // when, then
        assertThrows(DuplicateResourceException.class,
                () -> basicUserService.updateUserInfo(userId, request, null));
        assertThat(user.getUsername()).isEqualTo("test");

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByUsername("update");
        then(userRepository).should(never()).existsByEmail(any());
        then(userRepository).should(never()).saveAndFlush(any());
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("중복된 email으로 수정하면 예외를 던집니다.")
    void if_new_email_is_already_exists_throw_exception() {
        // given
        UUID userId = UUID.randomUUID();
        UpdateUserRequestDTO request =
                new UpdateUserRequestDTO(null, "update@test.com", null);

        // 기존 유저
        User user = new User("test", "test@test.com", "1234", null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("update@test.com")).willReturn(true);

        // when, then
        assertThrows(DuplicateResourceException.class,
                () -> basicUserService.updateUserInfo(userId, request, null));
        assertThat(user.getEmail()).isEqualTo("test@test.com");

        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail("update@test.com");
        then(userRepository).should(never()).saveAndFlush(any());
        then(userMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하는 유저는 삭제할 수 있습니다.")
    void exists_user_can_be_deleted() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("test", "test@test.com", "1234", null);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        basicUserService.deleteUser(userId);

        // then
        then(userRepository).should().findById(userId);
        then(userRepository).should().deleteById(userId);
        // 프로필이 없는 경우라
        then(binaryContentRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("프로필이 있는 유저도 삭제할 수 있습니다.")
    void delete_user_with_profile() {
        // given
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        BinaryContent profile = mock(BinaryContent.class);
        given(profile.getId()).willReturn(profileId);
        User user = new User("test", "test@test.com", "1234", profile);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        basicUserService.deleteUser(userId);

        // then
        then(binaryContentRepository).should().deleteById(profile.getId());
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 유저는 삭제할 수 없습니다.")
    void not_found_user_can_not_be_deleted() {
        // given
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when, then
        assertThrows(UserNotFoundException.class,
                () -> basicUserService.deleteUser(userId));
        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).deleteById(userId);
    }
}