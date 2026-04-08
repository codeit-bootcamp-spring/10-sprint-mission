package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@Import({GlobalExceptionHandler.class, PageResponseMapper.class})
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MessageService messageService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("GET /api/messages 성공 - 슬라이스 JSON 응답")
  void getMessages_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UserDto author = UserDto.builder()
        .id(authorId)
        .username("writer-alice")
        .email("writer-alice@email.com")
        .online(true)
        .build();

    MessageDto first = new MessageDto(
        UUID.randomUUID(),
        Instant.parse("2026-03-30T10:00:00Z"),
        Instant.parse("2026-03-30T10:00:00Z"),
        "hello",
        channelId,
        author,
        List.of()
    );
    MessageDto second = new MessageDto(
        UUID.randomUUID(),
        Instant.parse("2026-03-30T10:01:00Z"),
        Instant.parse("2026-03-30T10:01:00Z"),
        "world",
        channelId,
        author,
        List.of()
    );

    Slice<MessageDto> slice = new SliceImpl<>(
        List.of(first, second),
        PageRequest.of(0, 2),
        true
    );

    given(messageService.findAllByChannelId(eq(channelId), isNull(), eq(2))).willReturn(slice);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].content").value("hello"))
        .andExpect(jsonPath("$.content[1].content").value("world"))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.nextCursor").value("2026-03-30T10:01:00Z"));
  }

  @Test
  @DisplayName("GET /api/messages/{id} 실패 - 존재하지 않는 메시지")
  void getMessage_Fail_MessageNotFound() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    given(messageService.getMessage(messageId)).willThrow(new MessageNotFoundException(messageId));

    // when & then
    mockMvc.perform(get("/api/messages/{id}", messageId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("M001"))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.details.messageId").value(messageId.toString()));
  }
}
