package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.response.ApiException;
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
        // given
        CreateUserRequest request = new CreateUserRequest(
                "testUser" + UUID.randomUUID(),
                "1234",
                UUID.randomUUID() + "@test.com",
                binaryContentRequest
        );

        // when
        UUID userId = basicUserService.createUser(request);

        // then
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
            assertThat(binaryContent.getBinaryContentOwnerType())
                    .isEqualTo(BinaryContentOwnerType.USER);
            assertThat(binaryContent.getBytes()).isEqualTo(binaryContentRequest.file().getBytes());
        }
    }

    static Stream<Arguments> createUserBinaryContentRequestProvider() {
        return Stream.of(
                Arguments.of((BinaryContentRequest) null),
                Arguments.of(new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile(
                                "file",
                                "test.png",
                                "image/png",
                                "test-bytes".getBytes()
                        )
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("duplicateUserProvider")
    @DisplayName("username 또는 email 중복 시 User 생성 실패")
    void testCreateUserDuplicateFail(CreateUserRequest first, CreateUserRequest duplicate) {
        // given
        basicUserService.createUser(first);

        // when + then
        assertThatThrownBy(() -> basicUserService.createUser(duplicate))
                .isInstanceOf(ApiException.class);
    }

    static Stream<Arguments> duplicateUserProvider() {
        String username = "dupUser";
        String email = "dup@test.com";

        return Stream.of(
                // username 중복
                Arguments.of(
                        new CreateUserRequest(
                                username,
                                "1234",
                                "a@test.com",
                                null
                        ),
                        new CreateUserRequest(
                                username,
                                "5678",
                                "b@test.com",
                                null
                        )
                ),

                // email 중복
                Arguments.of(
                        new CreateUserRequest(
                                "userA",
                                "1234",
                                email,
                                null
                        ),
                        new CreateUserRequest(
                                "userB",
                                "5678",
                                email,
                                null
                        )
                )
        );
    }

    @Test
    @DisplayName("User 단건 조회 성공")
    void testFindUserByUserId() {
        // given
        CreateUserRequest request = new CreateUserRequest(
                "findUser",
                "1234",
                "find@test.com",
                null
        );

        UUID userId = basicUserService.createUser(request);

        // when
        UserResponse response = basicUserService.findUserByUserID(userId);

        // then
        assertThat(response.username()).isEqualTo("findUser");
        assertThat(response.online()).isTrue();
    }

    @Test
    @DisplayName("User 전체 조회 성공")
    void testFindAllUsers() {
        // given
        basicUserService.createUser(new CreateUserRequest(
                "u1", "1234", "u1@test.com", null
        ));

        basicUserService.createUser(new CreateUserRequest(
                "u2", "1234", "u2@test.com", null
        ));

        // when
        List<UserResponse> users = basicUserService.findAllUsers();

        // then
        assertThat(users).hasSize(2);

        assertThat(users)
                .extracting(UserResponse::username)
                .containsExactlyInAnyOrder("u1", "u2");
    }

    @Test
    @DisplayName("User 수정 성공 - 프로필 이미지 포함")
    void testUpdateUserWithProfileImage() throws Exception {
        // given
        UUID userId = basicUserService.createUser(
                new CreateUserRequest(
                        "beforeImg",
                        "1234",
                        "img@test.com",
                        null
                )
        );

        BinaryContentRequest imageRequest =
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile(
                                "file",
                                "update.png",
                                "image/png",
                                "update-bytes".getBytes()
                        )
                );

        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "afterImg",
                "5678",
                "afterImg@test.com",
                imageRequest
        );

        // when
        UserResponse response = basicUserService.updateUser(userId, updateRequest);

        // then
        User user = userRepository.findById(userId).orElseThrow();

        assertThat(user.getProfileId()).isNotNull();

        BinaryContent content =
                binaryContentRepository.findById(user.getProfileId()).orElseThrow();

        assertThat(content.getOwnerId()).isEqualTo(userId);
        assertThat(content.getBytes()).isEqualTo(imageRequest.file().getBytes());

        assertThat(response.username()).isEqualTo("afterImg");
    }

    @Test
    @DisplayName("User 수정 성공 - 프로필 이미지 미포함 시 기존 이미지 유지")
    void testUpdateUserWithoutProfileImage_keepExisting() {
        // given
        BinaryContentRequest imageRequest =
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile(
                                "file",
                                "orig.png",
                                "image/png",
                                "orig-bytes".getBytes()
                        )
                );

        UUID userId = basicUserService.createUser(
                new CreateUserRequest(
                        "origUser",
                        "1234",
                        "orig@test.com",
                        imageRequest
                )
        );

        User before = userRepository.findById(userId).orElseThrow();
        UUID existingProfileId = before.getProfileId();

        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "updatedUser",
                "5678",
                "updated@test.com",
                null
        );

        // when
        basicUserService.updateUser(userId, updateRequest);

        // then
        User after = userRepository.findById(userId).orElseThrow();
        assertThat(after.getProfileId()).isEqualTo(existingProfileId);
    }

    @Test
    @DisplayName("User 수정 실패 - username 중복")
    void testUpdateUserDuplicateUsernameFail() {
        // given
        UUID userId1 = basicUserService.createUser(
                new CreateUserRequest(
                        "dupUser1",
                        "1234",
                        "dup1@test.com",
                        null
                )
        );

        UUID userId2 = basicUserService.createUser(
                new CreateUserRequest(
                        "dupUser2",
                        "1234",
                        "dup2@test.com",
                        null
                )
        );

        UpdateUserRequest updateRequest =
                new UpdateUserRequest(
                        "dupUser1",
                        "5678",
                        "new2@test.com",
                        null
                );

        // when + then
        assertThatThrownBy(() -> basicUserService.updateUser(userId2, updateRequest))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("User 수정 실패 - email 중복")
    void testUpdateUserDuplicateEmailFail() {
        // given
        UUID userId1 = basicUserService.createUser(
                new CreateUserRequest(
                        "emailUser1",
                        "1234",
                        "email1@test.com",
                        null
                )
        );

        UUID userId2 = basicUserService.createUser(
                new CreateUserRequest(
                        "emailUser2",
                        "1234",
                        "email2@test.com",
                        null
                )
        );

        UpdateUserRequest updateRequest =
                new UpdateUserRequest(
                        "newName",
                        "5678",
                        "email1@test.com",
                        null
                );

        // when + then
        assertThatThrownBy(() -> basicUserService.updateUser(userId2, updateRequest))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("User 삭제 성공")
    void testDeleteUser() {
        // given
        BinaryContentRequest imageRequest =
                new BinaryContentRequest(
                        BinaryContentOwnerType.USER,
                        new MockMultipartFile(
                                "file",
                                "delete.png",
                                "image/png",
                                "delete-bytes".getBytes()
                        )
                );

        UUID userId = basicUserService.createUser(
                new CreateUserRequest(
                        "deleteUser",
                        "1234",
                        "delete@test.com",
                        imageRequest
                )
        );

        User user = userRepository.findById(userId).orElseThrow();
        UUID profileId = user.getProfileId();

        // when
        basicUserService.deleteUser(userId);

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(userStatusRepository.findByUserId(userId)).isEmpty();

        if (profileId != null) {
            assertThat(binaryContentRepository.findById(profileId)).isEmpty();
        }
    }
}
