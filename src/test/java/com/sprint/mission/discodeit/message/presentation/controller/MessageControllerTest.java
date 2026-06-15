package com.sprint.mission.discodeit.message.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.api.response.PageResponse;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.response.MessageResponse;
import com.sprint.mission.discodeit.message.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MessageService messageService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("create - 메시지를 성공적으로 생성한다.")
  void create_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId, List.of());
    MessageResponse response = new MessageResponse(UUID.randomUUID(), Instant.now(), Instant.now(),
        "Hello", channelId, null, Set.of());

    given(messageService.create(any(MessageCreateRequest.class))).willReturn(response);

    mockMvc.perform(post("/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello"))
        .andDo(print());

    ArgumentCaptor<MessageCreateRequest> captor = ArgumentCaptor.forClass(MessageCreateRequest.class);
    verify(messageService).create(captor.capture());
    assertThat(captor.getValue().getContent()).isEqualTo("Hello");
    assertThat(captor.getValue().getChannelId()).isEqualTo(channelId);
    assertThat(captor.getValue().getAuthorId()).isEqualTo(authorId);
  }

  @Test
  @DisplayName("create - 필수 값이 없으면 400 Bad Request를 반환한다.")
  void create_fail_validation() throws Exception {
    MessageCreateRequest request = new MessageCreateRequest("", null, null, List.of());

    mockMvc.perform(post("/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("findInChannel - 채널 내의 메시지를 성공적으로 조회한다.")
  void findInChannel_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    MessageResponse response = new MessageResponse(UUID.randomUUID(), Instant.now(), Instant.now(),
        "Hello", channelId, null, Set.of());
    PageResponse<MessageResponse> pageResponse = new PageResponse<>(List.of(response), 1, 50, false,
        100L);

    given(messageService.findSliceByChannelId(eq(channelId), any(Pageable.class))).willReturn(
        pageResponse);

    mockMvc.perform(get("/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].content").value("Hello"))
        .andDo(print());
  }

  @Test
  @DisplayName("update - 메시지를 성공적으로 수정한다.")
  void update_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    // MessageUpdateRequest JSON property is newContent
    String requestContent = "{\"newContent\":\"Updated Hello\"}";
    MessageResponse response = new MessageResponse(messageId, Instant.now(), Instant.now(),
        "Updated Hello", UUID.randomUUID(), null, Set.of());

    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class))).willReturn(
        response);

    mockMvc.perform(patch("/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestContent))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated Hello"))
        .andDo(print());

    ArgumentCaptor<MessageUpdateRequest> captor = ArgumentCaptor.forClass(MessageUpdateRequest.class);
    verify(messageService).update(eq(messageId), captor.capture());
    assertThat(captor.getValue().getContent()).isEqualTo("Updated Hello");
  }

  @Test
  @DisplayName("update - 내용이 비어있으면 400 Bad Request를 반환한다.")
  void update_fail_validation() throws Exception {
    UUID messageId = UUID.randomUUID();
    String requestContent = "{\"newContent\":\"\"}";

    mockMvc.perform(patch("/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestContent))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("delete - 메시지를 성공적으로 삭제한다.")
  void delete_success() throws Exception {
    UUID messageId = UUID.randomUUID();

    mockMvc.perform(delete("/messages/{messageId}", messageId))
        .andExpect(status().isNoContent())
        .andDo(print());

    verify(messageService).delete(messageId);
  }
}
