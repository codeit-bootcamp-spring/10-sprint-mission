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
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.message.application.service.MessageService;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.support.mapper.GlobalApiMapperTestConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalApiMapperTestConfig.class)
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
    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId,
        List.of());
    Channel channel = Channel.builder().build();
    org.springframework.test.util.ReflectionTestUtils.setField(channel, "id", channelId);
    Message message = Message.builder().content("Hello").channel(channel).build();

    given(messageService.create(any(MessageCreateRequest.class))).willReturn(message);

    mockMvc.perform(post("/api/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Hello"))
        .andDo(print());

    ArgumentCaptor<MessageCreateRequest> captor = ArgumentCaptor.forClass(
        MessageCreateRequest.class);
    verify(messageService).create(captor.capture());
    assertThat(captor.getValue().getContent()).isEqualTo("Hello");
    assertThat(captor.getValue().getChannelId()).isEqualTo(channelId);
    assertThat(captor.getValue().getAuthorId()).isEqualTo(authorId);
  }

  @Test
  @DisplayName("create - 필수 값이 없으면 400 Bad Request를 반환한다.")
  void create_fail_validation() throws Exception {
    MessageCreateRequest request = new MessageCreateRequest("", null, null, List.of());

    mockMvc.perform(post("/api/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("findInChannel - 채널 내의 메시지를 성공적으로 조회한다.")
  void findInChannel_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    Channel channel = Channel.builder().build();
    org.springframework.test.util.ReflectionTestUtils.setField(channel, "id", channelId);
    Message message = Message.builder().content("Hello").channel(channel).build();
    org.springframework.data.domain.Slice<Message> messageSlice = new org.springframework.data.domain.SliceImpl<>(
        List.of(message));

    given(messageService.findSliceByChannelId(eq(channelId), any(Pageable.class))).willReturn(
        messageSlice);

    mockMvc.perform(get("/api/messages")
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
    Channel channel = Channel.builder().build();
    org.springframework.test.util.ReflectionTestUtils.setField(channel, "id", UUID.randomUUID());
    Message message = Message.builder().content("Updated Hello").channel(channel).build();
    org.springframework.test.util.ReflectionTestUtils.setField(message, "id", messageId);

    given(messageService.update(eq(messageId), any(MessageUpdateRequest.class))).willReturn(
        message);

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestContent))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated Hello"))
        .andDo(print());

    ArgumentCaptor<MessageUpdateRequest> captor = ArgumentCaptor.forClass(
        MessageUpdateRequest.class);
    verify(messageService).update(eq(messageId), captor.capture());
    assertThat(captor.getValue().getContent()).isEqualTo("Updated Hello");
  }

  @Test
  @DisplayName("update - 내용이 비어있으면 400 Bad Request를 반환한다.")
  void update_fail_validation() throws Exception {
    UUID messageId = UUID.randomUUID();
    String requestContent = "{\"newContent\":\"\"}";

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestContent))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("delete - 메시지를 성공적으로 삭제한다.")
  void delete_success() throws Exception {
    UUID messageId = UUID.randomUUID();

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent())
        .andDo(print());

    verify(messageService).delete(messageId);
  }
}
