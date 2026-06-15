package com.sprint.mission.discodeit.readstatus.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.readstatus.application.service.ReadStatusService;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.readstatus.presentation.dto.response.ReadStatusResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReadStatusController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReadStatusControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ReadStatusService readStatusService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("findByUserId - 유저 ID로 읽음 상태를 정상 조회한다.")
  void findByUserId_success() throws Exception {
    UUID userId = UUID.randomUUID();
    ReadStatusResponse response = new ReadStatusResponse(UUID.randomUUID(), userId,
        UUID.randomUUID(), Instant.now(), true);

    given(readStatusService.findAllByUserId(userId)).willReturn(List.of(response));

    mockMvc.perform(get("/readStatuses")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value(userId.toString()));
  }

  @Test
  @DisplayName("create - 읽음 상태를 정상 생성한다.")
  void create_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    Instant now = Instant.now();
    
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, now);
    ReadStatusResponse response = new ReadStatusResponse(UUID.randomUUID(), userId, channelId, now, true);

    ArgumentCaptor<ReadStatusCreateRequest> captor = ArgumentCaptor.forClass(ReadStatusCreateRequest.class);
    given(readStatusService.create(captor.capture())).willReturn(response);

    mockMvc.perform(post("/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(userId.toString()));

    ReadStatusCreateRequest captured = captor.getValue();
    assertThat(captured.getUserId()).isEqualTo(userId);
    assertThat(captured.getChannelId()).isEqualTo(channelId);
    assertThat(captured.getLastReadAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("create - userId가 누락된 경우 400 Bad Request를 반환한다.")
  void create_fail_validation() throws Exception {
    UUID channelId = UUID.randomUUID();
    Instant now = Instant.now();
    
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(null, channelId, now);

    mockMvc.perform(post("/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("update - 읽음 상태를 정상 수정한다.")
  void update_success() throws Exception {
    UUID readStatusId = UUID.randomUUID();
    Instant now = Instant.now();
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(now, true);
    ReadStatusResponse response = new ReadStatusResponse(readStatusId, UUID.randomUUID(),
        UUID.randomUUID(), now, true);

    ArgumentCaptor<ReadStatusUpdateRequest> captor = ArgumentCaptor.forClass(ReadStatusUpdateRequest.class);
    given(readStatusService.update(eq(readStatusId), captor.capture())).willReturn(response);

    mockMvc.perform(patch("/readStatuses/{readStatusId}", readStatusId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(readStatusId.toString()));

    ReadStatusUpdateRequest captured = captor.getValue();
    assertThat(captured.getLastReadAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("update - lastReadAt이 누락된 경우 400 Bad Request를 반환한다.")
  void update_fail_validation() throws Exception {
    UUID readStatusId = UUID.randomUUID();
    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(null, true);

    mockMvc.perform(patch("/readStatuses/{readStatusId}", readStatusId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
