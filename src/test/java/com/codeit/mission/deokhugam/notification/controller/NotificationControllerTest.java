package com.codeit.mission.deokhugam.notification.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.mission.deokhugam.error.GlobalExceptionHandler;
import com.codeit.mission.deokhugam.notification.dto.request.NotificationRequestQuery;
import com.codeit.mission.deokhugam.notification.dto.request.NotificationUpdateRequest;
import com.codeit.mission.deokhugam.notification.dto.response.CursorPageResponseNotificationDto;
import com.codeit.mission.deokhugam.notification.dto.response.NotificationDto;
import com.codeit.mission.deokhugam.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WebMvcTest(NotificationController.class)
@Import(GlobalExceptionHandler.class)
public class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private NotificationService notificationService;

  @Nested
  @DisplayName("알림 목록 조회 API")
  class FindAllTest {

    @Test
    @DisplayName("성공")
    void findAllSuccess() throws Exception {
      // given
      UUID userId = UUID.randomUUID(); // 알림을 조회할 유저의 id

      NotificationDto content = NotificationDto.builder()
        .id((UUID.randomUUID()))
        .userId(userId)
        .reviewId(UUID.randomUUID())
        .reviewContent("리뷰 내용")
        .message("알림이 왔어요")
        .confirmed(false)
        .createdAt(Instant.now())
        .build();

      CursorPageResponseNotificationDto response = CursorPageResponseNotificationDto.builder()
        .content(List.of(content))
        .nextCursor(null)
        .nextAfter(null)
        .size(10)
        .totalElements(1)
        .hasNext(false)
        .build();

      given(notificationService.findByUserId(eq(userId),
        any(NotificationRequestQuery.class))).willReturn(response);

      // when & then
      mockMvc.perform(
          get("/api/notifications")
            .param("userId", userId.toString())
            .param("direction", "DESC")
            .param("limit", "20")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].userId").value(userId.toString()))
        .andExpect(jsonPath("$.nextCursor").value(nullValue()))
        .andExpect(jsonPath("$.nextAfter").value(nullValue()))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.hasNext").value(false))
      ;
    }

    @Test
    @DisplayName("userId를 전달하지 않은 경우 400을 반환")
    void findAllSuccessUserIdNotFound() throws Exception {
      // when & then
      mockMvc.perform(
          get("/api/notifications")
            .param("direction", "DESC")
            .param("limit", "20")
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 형식의 userId를 전달한 경우 400을 반환")
    void findByUserId_fail_invalidUserId() throws Exception {

      // when & then
      mockMvc.perform(
          get("/api/notifications")
            .param("userId", "invalid-uuid")
            .param("direction", "DESC")
            .param("limit", "20")
        )
        .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("단건 알림 읽음 상태 업데이트 API")
  class UpdateTest {

    @Test
    @DisplayName("알림 읽음 상태 업데이트 성공")
    void updateByUserIdSuccess() throws Exception {
      // given
      UUID requestUserId = UUID.randomUUID();
      UUID notificationId = UUID.randomUUID();

      NotificationUpdateRequest request = new NotificationUpdateRequest(true);

      NotificationDto response =
        NotificationDto.builder()
          .id(notificationId)
          .userId(requestUserId)
          .confirmed(true)
          .build();

      given(notificationService.updateById(
        eq(notificationId),
        eq(requestUserId),
        any(NotificationUpdateRequest.class)
      )).willReturn(response);

      // when & then
      mockMvc.perform(
          patch("/api/notifications/{notificationId}", notificationId)
            .header("Deokhugam-Request-User-ID", requestUserId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(notificationId.toString()))
        .andExpect(jsonPath("$.userId").value(requestUserId.toString()));

    }

    @Test
    @DisplayName("요청 유저 id 헤더가 없는 경우 400을 반환")
    void updateByUserIdFailWithoutHeader() throws Exception {
      // given
      UUID notificationId = UUID.randomUUID();

      NotificationUpdateRequest request =
        new NotificationUpdateRequest(true);

      // when & then
      mockMvc.perform(
          patch("/api/notifications/{notificationId}", notificationId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("모든 알림 읽음 상태 업데이트 API")
  class UpdateAllTest {

    @Test
    @DisplayName("모든 알림 읽음 처리 성공")
    void updateAllSuccess() throws Exception {
      // given
      UUID userId = UUID.randomUUID();

      doNothing().when(notificationService)
        .updateByUserId(userId);

      // when & then
      mockMvc.perform(
          patch("/api/notifications/read-all")
            .header("Deokhugam-Request-User-ID", userId)
        )
        .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("요청 유저 id 헤더가 없는 경우 400을 반환")
    void updateAllFailWithoutHeader() throws Exception {
      // given
      UUID notificationId = UUID.randomUUID();

      NotificationUpdateRequest request =
        new NotificationUpdateRequest(true);

      // when & then
      mockMvc.perform(
          patch("/api/notifications/{notificationId}", notificationId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest());
    }
  }
}
