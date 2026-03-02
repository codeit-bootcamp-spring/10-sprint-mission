package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserOnlineStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class BasicUserStatusServiceTest {

    @Autowired
    private BasicUserStatusService userStatusService;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        FileIOHelper.flushData();

        User user = new User("testUser", "password", "test@test.com");
        userRepository.save(user);
        userId = user.getId();
    }

    @Test
    @DisplayName("UserStatus 생성 성공")
    void createUserStatus_success() {
        UUID userStatusId = userStatusService.createUserStatus(new CreateUserStatusRequest(userId));
        UserStatus saved = userStatusRepository.findById(userStatusId).orElseThrow();
        assertThat(saved.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("존재하지 않는 유저면 UserStatus 생성 실패")
    void createUserStatus_fail_user_not_exist() {
        assertThatThrownBy(() -> userStatusService.createUserStatus(new CreateUserStatusRequest(UUID.randomUUID())))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("이미 UserStatus 존재하면 생성 실패")
    void createUserStatus_fail_duplicate() {
        CreateUserStatusRequest request = new CreateUserStatusRequest(userId);
        userStatusService.createUserStatus(request);

        assertThatThrownBy(() -> userStatusService.createUserStatus(request))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("userStatusId로 조회 성공")
    void findUserStatusById_success() {
        UUID id = userStatusService.createUserStatus(new CreateUserStatusRequest(userId));
        UserStatus response = userStatusService.findUserStatusByUserStatusId(id);
        assertThat(response.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("존재하지 않는 userStatusId 조회 실패")
    void findUserStatusById_fail() {
        assertThatThrownBy(() -> userStatusService.findUserStatusByUserStatusId(UUID.randomUUID()))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("모든 UserStatus 조회 성공")
    void findAllUserStatus_success() {
        UUID id1 = userStatusService.createUserStatus(new CreateUserStatusRequest(userId));

        User user2 = new User("testUser2", "password2", "test2@test.com");
        userRepository.save(user2);

        UUID id2 = userStatusService.createUserStatus(new CreateUserStatusRequest(user2.getId()));

        List<UserStatus> responses = userStatusService.findAllUserStatus();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(UserStatus::getId).containsExactlyInAnyOrder(id1, id2);
    }

    @Test
    @DisplayName("userId로 UserStatus 활성화 갱신 성공")
    void updateUserStatusByUserId_success() {
        userStatusService.createUserStatus(new CreateUserStatusRequest(userId));

        UserStatus response = userStatusService.updateUserStatusByUserId(
                userId,
                new UserStatusUpdateRequest(Instant.now())
        );

        assertThat(response.getOnlineStatus()).isEqualTo(UserOnlineStatus.ONLINE);
    }

    @Test
    @DisplayName("존재하지 않는 userId 업데이트 실패")
    void updateUserStatusByUserId_fail() {
        assertThatThrownBy(() -> userStatusService.updateUserStatusByUserId(
                UUID.randomUUID(),
                new UserStatusUpdateRequest(Instant.now())
        )).isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("lastActiveAt 시간이 지나면 UserStatus 상태 OFFLINE 으로 변경")
    void userStatus_becomesOffline_afterTimeout() {
        UserStatus tmp = new UserStatus(UUID.randomUUID(), Instant.now().minusSeconds(3600));
        userStatusRepository.save(tmp);

        UserStatus response = userStatusService.findUserStatusByUserStatusId(tmp.getId());

        assertThat(response.getOnlineStatus()).isEqualTo(UserOnlineStatus.OFFLINE);
    }

    @Test
    @DisplayName("UserStatus 삭제 성공")
    void deleteUserStatus_success() {
        UUID id = userStatusService.createUserStatus(new CreateUserStatusRequest(userId));
        userStatusService.deleteUserStatus(id);
        assertThat(userStatusRepository.findById(id)).isEmpty();
    }
}
