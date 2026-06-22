package com.sprint.mission.discodeit.notification.presentation.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.notification.application.service.NotificationService;
import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import com.sprint.mission.discodeit.support.mapper.GlobalApiMapperTestConfig;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalApiMapperTestConfig.class)
class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private NotificationService service;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("find - 사용자의 알림 목록을 정상 조회한다.")
  void find_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userId.toString(), null, List.of());

    User user = User.builder().username("test").email("test@test.com")
        .role(com.sprint.mission.discodeit.user.domain.entity.constant.UserRole.USER).build();
    org.springframework.test.util.ReflectionTestUtils.setField(user, "id", userId);
    Notification noti = Notification.builder().receiver(user).title("title").content("content")
        .build();

    given(service.find(userId)).willReturn(List.of(noti));

    mockMvc.perform(get("/api/notifications")
            .principal(authentication))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("title"))
        .andExpect(jsonPath("$[0].content").value("content"));
  }

  @Test
  @DisplayName("delete - 특정 알림을 정상 삭제한다.")
  void delete_success() throws Exception {
    UUID id = UUID.randomUUID();

    willDoNothing().given(service).delete(id);

    mockMvc.perform(delete("/api/notifications/{id}", id))
        .andExpect(status().isNoContent());
  }
}
