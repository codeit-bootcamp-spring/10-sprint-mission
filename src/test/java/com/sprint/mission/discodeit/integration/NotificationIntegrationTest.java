package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;

    private UUID persistUser() {
        User user = userRepository.save(
                new User("user-" + UUID.randomUUID(), UUID.randomUUID() + "@test.com", "pw"));
        return user.getId();
    }

    private RequestPostProcessor authOf(UUID userId) {
        UserDto userDto = new UserDto(userId, "user-" + userId, userId + "@test.com", null, Role.USER, true);
        DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "pw");
        return authentication(new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()));
    }

    @Test
    @DisplayName("인증된 사용자는 본인 알림 목록을 조회한다.")
    void findMine_authenticated_returnsList() throws Exception {
        UUID userId = persistUser();
        notificationService.create(userId, "t1", "c1");
        notificationService.create(userId, "t2", "c2");

        mockMvc.perform(get("/api/notifications").with(authOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("미인증 사용자의 알림 조회는 401 오류로 거부된다.")
    void findMine_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("본인 알림을 삭제하면 204, 이후 목록에서 사라진다.")
    void delete_ownNotification_returns204() throws Exception {
        UUID userId = persistUser();
        NotificationDto created = notificationService.create(userId, "t", "c");

        mockMvc.perform(delete("/api/notifications/{id}", created.id())
                        .with(authOf(userId)).with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(notificationService.findAllByReceiverId(userId)).isEmpty();
    }

    @Test
    @DisplayName("미인증 상태의 알림 삭제는 거부된다. (401)")
    void delete_unauthenticated_returns401() throws Exception {
        mockMvc.perform(delete("/api/notifications/{id}", UUID.randomUUID()).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("타인의 알림 삭제는 거부된다. (403)")
    void delete_othersNotification_returns403() throws Exception {
        UUID ownerId = persistUser();
        UUID otherId = UUID.randomUUID();
        NotificationDto created = notificationService.create(ownerId, "t", "c");

        mockMvc.perform(delete("/api/notifications/{id}", created.id())
                        .with(authOf(otherId)).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("존재하지 않는 알림 삭제는 404.")
    void delete_missingNotification_returns404() throws Exception {
        UUID userId = persistUser();

        mockMvc.perform(delete("/api/notifications/{id}", UUID.randomUUID())
                        .with(authOf(userId)).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
