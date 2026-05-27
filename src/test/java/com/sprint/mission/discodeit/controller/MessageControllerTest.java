package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.security.Role;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private BinaryContentService binaryContentService;

    private UUID messageId;
    private UUID channelId;
    private UUID authorId;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        UserDto authorDto = new UserDto(authorId, "달선", "dalsun@naver.com", null, true, Role.USER);
        messageDto = new MessageDto(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                "딸롱",
                UUID.randomUUID(),
                authorDto,
                List.of()
        );
    }

    @Test
    @DisplayName("메시지 전송 성공 - 멀티파트 요청")
    void send_success() throws Exception {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "안녕");

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest", "", "application/json",
                objectMapper.writeValueAsBytes(request));

        when(messageService.create(any(MessageCreateRequest.class), any())).thenReturn(messageDto);

        // when, then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("딸롱"));
    }

    @Test
    @DisplayName("메시지 전송 실패 - content 누락")
    void send_fail_empty_content() throws Exception {
        // given
        MessageCreateRequest request = new MessageCreateRequest(UUID.randomUUID(),
                UUID.randomUUID(), "");
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest", "", "application/json",
                objectMapper.writeValueAsBytes(request));

        // when, then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("메시지 수정 성공")
    void update_success() throws Exception {
        // given
        MessageUpdateRequest updateDto = new MessageUpdateRequest("수정");

        when(messageService.update(eq(messageId), any(MessageUpdateRequest.class))).thenReturn(
                messageDto);

        // when, then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("딸롱"));
    }

    @Test
    @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지 ID")
    void update_fail_not_found() throws Exception {
        // given
        MessageUpdateRequest updateDto = new MessageUpdateRequest("내용");

        when(messageService.update(eq(messageId), any()))
                .thenThrow(new MessageNotFoundException(messageId));

        // when, then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void delete_success() throws Exception {
        // given
        doNothing().when(messageService).delete(messageId);

        // when, then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
    void delete_fail_message_not_found() throws Exception {

        // given
        UUID messageId = UUID.randomUUID();

        doThrow(new MessageNotFoundException(messageId))
                .when(messageService).delete(messageId);

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/messages/{messageId}", messageId)
        );

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
    }


    @Test
    @DisplayName("메시지 목록 조회 성공 - PageResponse 구조 검증")
    void get_messages_success() throws Exception {
        // given
        PageResponse<MessageDto> response = PageResponse.<MessageDto>builder()
                .content(List.of(messageDto))
                .nextCursor(null)
                .size(50)
                .hasNext(false)
                .totalElements(1L)
                .build();

        when(messageService.getMessages(eq(channelId), any(),
                any(Pageable.class))).thenReturn(response);

        // when
        ResultActions actions = mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].content").value(messageDto.content()))
                .andExpect(jsonPath("$.size").value(50))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("메시지 조회 실패 - 존재하지 않는 채널")
    void getMessages_fail_channel_not_found() throws Exception {

        UUID channelId = UUID.randomUUID();

        when(messageService.getMessages(eq(channelId), any(), any()))
                .thenThrow(new ChannelNotFoundException(channelId));

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }
}
