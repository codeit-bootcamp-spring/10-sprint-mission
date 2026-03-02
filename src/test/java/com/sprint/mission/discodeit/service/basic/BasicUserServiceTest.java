package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserOnlineStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class BasicUserServiceTest {

    @Autowired
    private BasicUserService basicUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @BeforeEach
    void setup() {
        FileIOHelper.flushData();
    }

    @ParameterizedTest
    @MethodSource("createUserBinaryContentRequestProvider")
    @DisplayName("User 생성 성공")
    void testCreateUser(BinaryContentRequest binaryContentRequest) throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "testUser" + UUID.randomUUID(),
                UUID.randomUUID() + "@test.com",
                "1234"
        );

        UUID userId = basicUserService.createUser(request, binaryContentRequest).getId();

        User user = userRepository.findById(userId).orElseThrow();
        UserStatus status = userStatusRepository.findByUserId(userId).orElseThrow();

        assertThat(user.getUsername()).isEqualTo(request.username());
        assertThat(status.getUserId()).isEqualTo(user.getId());
        assertThat(status.getLastActiveAt()).isEqualTo(user.getUpdatedAt());
        assertThat(status.getOnlineStatus()).isEqualTo(UserOnlineStatus.ONLINE);

        if (binaryContentRequest == null) {
            assertThat(user.getProfileId()).isNull();
        } else {
            assertThat(user.getProfileId()).isNotNull();
            BinaryContent binaryContent = binaryContentRepository.findById(user.getProfileId()).orElseThrow();
            assertThat(binaryContent.getOwnerId()).isEqualTo(user.getId());
            assertThat(binaryContent.getBinaryContentOwnerType()).isEqualTo(BinaryContentOwnerType.USER);
            assertThat(binaryContent.getBytes()).isEqualTo(binaryContentRequest.file().getBytes());
        }
    }

    static Stream<Arguments> createUserBinaryContentRequestProvider() {
        return Stream.of(
                Arguments.of((BinaryContentRequest) null),
                Arguments.of(new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile("file", "test.png", "image/png", "test-bytes".getBytes())
                ))
        );
    }

    @Test
    @DisplayName("username 또는 email 중복 시 User 생성 실패")
    void testCreateUserDuplicateFail() {
        UserCreateRequest first = new UserCreateRequest("dupUser", "a@test.com", "1234");
        UserCreateRequest duplicateUsername = new UserCreateRequest("dupUser", "b@test.com", "5678");
        UserCreateRequest duplicateEmail = new UserCreateRequest("other", "a@test.com", "5678");

        basicUserService.createUser(first, null);

        assertThatThrownBy(() -> basicUserService.createUser(duplicateUsername, null))
                .isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> basicUserService.createUser(duplicateEmail, null))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("User 단건 조회 성공")
    void testFindUserByUserId() {
        UUID userId = basicUserService.createUser(
                new UserCreateRequest("findUser", "find@test.com", "1234"),
                null
        ).getId();

        User response = basicUserService.findUserByUserID(userId);

        assertThat(response.getUsername()).isEqualTo("findUser");
    }

    @Test
    @DisplayName("User 전체 조회 성공")
    void testFindAllUsers() {
        basicUserService.createUser(new UserCreateRequest("u1", "u1@test.com", "1234"), null);
        basicUserService.createUser(new UserCreateRequest("u2", "u2@test.com", "1234"), null);

        List<UserDto> users = basicUserService.findAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserDto::username).containsExactlyInAnyOrder("u1", "u2");
    }

    @Test
    @DisplayName("User 수정 성공 - 프로필 이미지 포함")
    void testUpdateUserWithProfileImage() throws Exception {
        UUID userId = basicUserService.createUser(
                new UserCreateRequest("beforeImg", "img@test.com", "1234"),
                null
        ).getId();

        BinaryContentRequest imageRequest = new BinaryContentRequest(
                BinaryContentOwnerType.USER,
                new MockMultipartFile("file", "update.png", "image/png", "update-bytes".getBytes())
        );

        UserUpdateRequest updateRequest = new UserUpdateRequest("afterImg", "afterImg@test.com", "5678");

        User response = basicUserService.updateUser(userId, updateRequest, imageRequest);

        User user = userRepository.findById(userId).orElseThrow();
        assertThat(user.getProfileId()).isNotNull();

        BinaryContent content = binaryContentRepository.findById(user.getProfileId()).orElseThrow();
        assertThat(content.getOwnerId()).isEqualTo(userId);
        assertThat(content.getBytes()).isEqualTo(imageRequest.file().getBytes());
        assertThat(response.getUsername()).isEqualTo("afterImg");
    }

    @Test
    @DisplayName("User 수정 성공 - 프로필 이미지 미포함 시 기존 이미지 유지")
    void testUpdateUserWithoutProfileImage_keepExisting() {
        BinaryContentRequest imageRequest = new BinaryContentRequest(
                BinaryContentOwnerType.USER,
                new MockMultipartFile("file", "orig.png", "image/png", "orig-bytes".getBytes())
        );

        UUID userId = basicUserService.createUser(
                new UserCreateRequest("origUser", "orig@test.com", "1234"),
                imageRequest
        ).getId();

        UUID existingProfileId = userRepository.findById(userId).orElseThrow().getProfileId();

        UserUpdateRequest updateRequest = new UserUpdateRequest("updatedUser", "updated@test.com", "5678");
        basicUserService.updateUser(userId, updateRequest, null);

        User after = userRepository.findById(userId).orElseThrow();
        assertThat(after.getProfileId()).isEqualTo(existingProfileId);
    }

    @Test
    @DisplayName("User 수정 실패 - username 중복")
    void testUpdateUserDuplicateUsernameFail() {
        UUID userId1 = basicUserService.createUser(
                new UserCreateRequest("dupUser1", "dup1@test.com", "1234"), null
        ).getId();

        UUID userId2 = basicUserService.createUser(
                new UserCreateRequest("dupUser2", "dup2@test.com", "1234"), null
        ).getId();

        UserUpdateRequest updateRequest = new UserUpdateRequest("dupUser1", "new2@test.com", "5678");

        assertThatThrownBy(() -> basicUserService.updateUser(userId2, updateRequest, null))
                .isInstanceOf(ApiException.class);

        assertThat(userRepository.findById(userId1)).isPresent();
    }
}
