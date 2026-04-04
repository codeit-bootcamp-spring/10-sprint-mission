package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserEmailDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNameDuplicateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    BinaryContentRepository binaryContentRepository;
    @Mock
    BinaryContentStorage binaryContentStorage;


    @InjectMocks
    private BasicUserService basicUserService;

    @Nested
    public class user_create_test {

        @Test
        @DisplayName("유저 생성 성공 (프로필 X)")
        void create_user_success_without_profile() {
            // given
            String username = "abc";
            String email = "abc@naver.com";
            String password = "abc";

            // 이메일 & 이름 중복 검증을 스터빙 (중복 안됨)
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(userRepository.existsByUsername(username)).thenReturn(false);

            // 유저 레포지토리에 save를 호출하여 성공하면 첫 번째 파라미터(any(User.class))를 반환.
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // UserStatus 레포지토리에 save를 호출하여 성공하면 첫 번째 파라미터(any(UserStatus.class))를 반환
            when(userStatusRepository.save(any(UserStatus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // 서비스 생성 요청에 사용될 CreateRequestDto
            UserCreateRequestDTO req = new UserCreateRequestDTO(username, email, password);

            // 성공적으로 유저 생성 시 예상되는 결과 UserDto
            UserDto expected = new UserDto(UUID.randomUUID(), username, email, null, false);

            // Service 마지막에는 영속화된 유저 객체를 Dto로 매핑하는 작업을 수행
            // 따라서 userMapper도 스터빙을 해줘야 함.
            // userMapper에 아무 User 클래스를 넣으면 expected가 리턴 됨.
            when(userMapper.toDto(any(User.class))).thenReturn(expected);

            // when
            UserDto result = basicUserService.create(req, null);

            // then
            assertNotNull(result); // 산출된 결과 값이 null이 아님을 검증
            verify(userRepository).existsByEmail(email); // verify : 이 메서드가 실제로 호출됐는지 검사하는 함수
            verify(userRepository).existsByUsername(username);
            verify(userRepository).save(any(User.class));
            verify(userStatusRepository).save(any(UserStatus.class));
            verify(userMapper).toDto(any(User.class));
            assertEquals(expected, result); // 마지막으로 예상 결과 값과 실제 값이 같은지 검증
        }

        @Test
        @DisplayName("유저 생성 성공 (프로필 O)")
        void create_user_with_profile() {
            // given
            String email = "abc@naver.com";
            String name = "abc";
            String password = "abc";
            UserCreateRequestDTO req = new UserCreateRequestDTO(name, email, password);

            // 이메일 & 이름 중복 검증 스터빙
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(userRepository.existsByUsername(name)).thenReturn(false);

            // User 영속화 스터빙
            when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // UserStatus 영속화 스터빙
            when(userStatusRepository.save(any(UserStatus.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            // 더미 데이터
            byte[] profileBytes = "fake-image-bytes".getBytes(StandardCharsets.UTF_8);

            // 더미 데이터를 기반으로 BinaryContentDto & BinaryContent 객체 생성
            BinaryContentDto profile = new BinaryContentDto(
                UUID.randomUUID(),
                "profile.png",
                profileBytes.length,
                "image/png",
                profileBytes);

            BinaryContent savedBinaryContent = new BinaryContent(
                profile.fileName(),
                profile.size(),
                profile.contentType()
            );

            savedBinaryContent.setId(UUID.randomUUID());

            // BinaryContent 관련 스터빙
            when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(
                savedBinaryContent);

            UserDto expected = new UserDto(UUID.randomUUID(), name, email, profile, true);
            when(userMapper.toDto(any(User.class))).thenReturn(expected);

            // when
            UserDto result = basicUserService.create(req, profile);

            // then
            assertNotNull(result);
            verify(binaryContentStorage).put(eq(savedBinaryContent.getId()), eq(profileBytes));
            verify(binaryContentRepository).save(any(BinaryContent.class));
            verify(userRepository).existsByEmail(email);
            verify(userRepository).existsByUsername(name);
            verify(userRepository).save(any(User.class));
            verify(userStatusRepository).save(any(UserStatus.class));
            verify(userMapper).toDto(any(User.class));
        }

        @Test
        @DisplayName("유저 생성 시, 중복된 이메일을 가질 시 커스텀 예외를 던집니다.")
        void create_user_with_duplicate_email() {
            // given
            String username1 = "abc";
            String email1 = "abc@naver.com";
            String password1 = "abc";
            UserCreateRequestDTO req1 = new UserCreateRequestDTO(username1, email1, password1);

            String username2 = "def";
            String email2 = "abc@naver.com"; // 이메일 중복!
            String password2 = "def";
            UserCreateRequestDTO req2 = new UserCreateRequestDTO(username2, email2, password2);

            when(userRepository.existsByUsername(username1)).thenReturn(false);
            when(userRepository.existsByUsername(username2)).thenReturn(false);
            // 첫 번째 생성은 통과(false), 두 번째 생성은 중복(true)
            when(userRepository.existsByEmail(email1)).thenReturn(false, true);
            when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
            when(userStatusRepository.save(any(UserStatus.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
            when(userMapper.toDto(any(User.class)))
                .thenReturn(new UserDto(UUID.randomUUID(), username1, email1, null, false));

            // when + then
            basicUserService.create(req1, null);
            assertThrows(UserEmailDuplicateException.class,
                () -> basicUserService.create(req2, null));
        }

        @Test
        @DisplayName("유저 생성 시, 이름이 중복일 시 커스텀 예외가 발생합니다.")
        void create_user_with_duplicate_name() {
            // given

            String name1 = "abc";
            String email1 = "abc@naver.com";
            String password1 = "abc";

            String email2 = "def@naver.com";
            String password2 = "def";

            UserCreateRequestDTO req1 = new UserCreateRequestDTO(name1, email1, password1);
            UserCreateRequestDTO req2 = new UserCreateRequestDTO(name1, email2, password2);

            when(userRepository.existsByUsername(name1)).thenReturn(false, true);
            when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
            when(userStatusRepository.save(any(UserStatus.class))).thenAnswer(
                invocation -> invocation.getArgument(0));
            when(userMapper.toDto(any(User.class))).thenReturn(
                new UserDto(UUID.randomUUID(), name1, email1, null, false));

            // when + then
            basicUserService.create(req1, null);
            assertThrows(UserNameDuplicateException.class,
                () -> basicUserService.create(req2, null));
        }
    }

    @Nested
    public class user_update_test {

        @Test
        @DisplayName("유저 업데이트 성공 (프로필 X)")
        void update_user_success() {
            // given
            UUID userId = UUID.randomUUID();
            String name = "abc";
            String email = "abc@naver.com";
            String password = "abc";

            String newName = "def";
            String newEmail = "def@naver.com";
            String newPassword = "def";

            UserUpdateDTO updateReq = new UserUpdateDTO(newName, newEmail, newPassword);

            User existingUser = new User(name, email, password, null);
            existingUser.setId(userId); // 유저 객체의 ID를 설정하여, 추후 findById에 사용할 수 있도록 함.

            // service의 update 메서드에서 findById가 한번 호출됨. 스터빙 필요
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            // 수정하려는 필드들의 값이 중복이 아니면서 자기 자신의 아이디와 검증
            when(userRepository.existsByEmailAndIdNot(newEmail, userId)).thenReturn(false);
            when(userRepository.existsByUsernameAndIdNot(newName, userId)).thenReturn(false);

            // 유저 영속화 스터빙
            when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

            // 유저 매퍼의 메소드를 스터빙
            // 파라미터로 들어온 User 객체를 바탕으로 UserDto를 뽑아냄.
            when(userMapper.toDto(any(User.class))).thenAnswer(invocation -> {
                User updated = invocation.getArgument(0);
                return new UserDto(updated.getId(), updated.getUsername(), updated.getEmail(), null,
                    false);
            });

            // when
            UserDto result = basicUserService.update(userId, updateReq, null);

            // then
            assertNotNull(result); // 결과 값이 null 아닌지 검증
            assertEquals(newName, result.username()); // 결과 값이 수정하고자 하는 이름과 맞는가?
            assertEquals(newEmail, result.email());
            verify(userRepository).findById(userId); // 업데이트 하면서 findById가 호출 되었는가?
            verify(userRepository).existsByEmailAndIdNot(newEmail, userId);
            verify(userRepository).existsByUsernameAndIdNot(newName, userId);
            verify(userRepository).save(any(User.class));
            verify(userMapper).toDto(any(User.class));
        }

        @Test
        @DisplayName("유저 업데이트 성공 (프로필 O)")
        void update_user_success_with_profile() {
            // given
            UUID userId = UUID.randomUUID();
            String name = "abc";
            String email = "abc@naver.com";
            String password = "abc";

            User existingUser = new User(name, email, password, null);
            existingUser.setId(userId);

            String newName = "def";
            String newEmail = "def@naver.com";
            String newPassword = "def";

            UserUpdateDTO req = new UserUpdateDTO(newName, newEmail, newPassword);

            // 업데이트 할 첨부 파일 정보
            byte[] bytes = "fake-profile-bytes".getBytes(StandardCharsets.UTF_8);
            BinaryContentDto profile = new BinaryContentDto(UUID.randomUUID(), "file.png",
                bytes.length, "Image/png", bytes);
            BinaryContent binaryContent = new BinaryContent("image.png", (long) bytes.length,
                "Image/png", bytes);

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(binaryContentRepository.save(any(BinaryContent.class))).thenReturn(binaryContent);

            when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

            when(userMapper.toDto(existingUser)).thenReturn(new UserDto(existingUser.getId(),
                existingUser.getUsername(), existingUser.getEmail(), profile, false));

            // when
            basicUserService.update(userId, req, profile);

            // then
            assertEquals(newName, existingUser.getUsername());
            assertEquals(newEmail, existingUser.getEmail());
            assertEquals(newPassword, existingUser.getPassword());
            assertEquals(binaryContent, existingUser.getProfile());
            verify(userRepository).findById(userId);
            verify(binaryContentRepository).save(any(BinaryContent.class));
            verify(userRepository).existsByUsernameAndIdNot(newName, userId);
            verify(userRepository).existsByEmailAndIdNot(newEmail, userId);
            verify(userRepository).save(existingUser);


        }

        @Test
        @DisplayName("중복된 이메일로 인한 유저 업데이트 실패")
        void update_user_fail_duplicate_email() {
            // given
            UUID userId = UUID.randomUUID();
            String name = "abc";
            String email = "abc@naver.com";
            String password = "abc";

            User existingUser = new User(name, email, password, null);
            existingUser.setId(userId);

            String newName = "def";
            String newEmail = "abc@naver.com";
            String newPassword = "abc";

            UserUpdateDTO request = new UserUpdateDTO(newName, newEmail, newPassword);

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmailAndIdNot(newEmail, userId)).thenThrow(
                new UserEmailDuplicateException());
            //doNothing().when(userRepository).existsByEmailAndIdNot(newName, userId);
//            when(userRepository.save(any(User.class))).thenAnswer(
//                invocation -> invocation.getArgument(0));
//            when(userMapper.toDto(any(User.class))).thenAnswer(
//                invocation -> {
//                    User updated = invocation.getArgument(0);
//                    return new UserDto(updated.getId(), updated.getUsername(), updated.getEmail(),
//                        null, false);
//                });

            // when + then
            assertThrows(UserEmailDuplicateException.class,
                () -> basicUserService.update(userId, request, null));
            verify(userRepository).findById(userId);
            verify(userRepository).existsByEmailAndIdNot(newEmail, existingUser.getId());
            //verify(userRepository).existsByUsernameAndIdNot(name, existingUser.getId());

        }
    }

    @Nested
    public class user_delete_test {

        @Test
        @DisplayName("유저 삭제 성공")
        void user_delete_success() {
            // given
            UUID userId = UUID.randomUUID(); // UserRepository.deleteById & findById에 쓰일 UUID
            // 이미 존재하고 있는 유저 객체 생성
            User existingUser = new User("abc", "abc@naver.com", "abc", null);
            existingUser.setId(userId); // 해당 유저의 UUID를 설정

            // userRepository.findById(userId)를 호출하면, existingUser를 Optional에 래핑해서 반환.
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

            // when + then
            assertDoesNotThrow(
                () -> basicUserService.delete(userId)); // service의 delete 호출 시 아무 예외도 던져지지 않음을 검증
            verify(userRepository).deleteById(userId);
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("없는 유저 삭제 시도 및 실패")
        void user_delete_nonexisting() {
            // given
            UUID userId = UUID.randomUUID();

            when(userRepository.findById(any(UUID.class))).thenThrow(UserNotFoundException.class);

            // when + then
            assertThrows(UserNotFoundException.class, () -> basicUserService.delete(userId));
            verify(userRepository).findById(any(UUID.class));
        }
    }

}
