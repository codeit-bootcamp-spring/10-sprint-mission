package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private MessageService messageService;

    @Test
    @DisplayName("메시지 생성 성공 시 201 Created 반환")
    void create_message_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        CreateMessageRequestDto request = new CreateMessageRequestDto("Hello", channelId, authorId, null);

        UserDto authorDto = new UserDto(authorId, "tester", "test@test.com", null, Role.USER, true);
        MessageDto response = new MessageDto(UUID.randomUUID(), request.content(), channelId, authorDto, List.of(), Instant.now(), Instant.now());

        given(messageService.create(any(), any())).willReturn(response);

        // MessageController의 @RequestPart("messageCreateRequest")와 일치하도록 세팅
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest",
            "request.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Hello"));
    }

    @Test
    @DisplayName("메시지 수정 성공 시 200 OK 반환")
    void update_message_success() throws Exception {
        UUID messageId = UUID.randomUUID();
        UpdateMessageRequestDto request = new UpdateMessageRequestDto("Updated Content");

        UserDto authorDto = new UserDto(UUID.randomUUID(), "tester", "test@test.com", null, Role.USER, true);
        MessageDto response = new MessageDto(messageId, request.newContent(), UUID.randomUUID(), authorDto, List.of(), Instant.now(), Instant.now());

        given(messageService.update(eq(messageId), any())).willReturn(response);

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Updated Content"));
    }

    @Test
    @DisplayName("메시지 삭제 성공 시 204 No Content 반환")
    void delete_message_success() throws Exception {
        UUID messageId = UUID.randomUUID();

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("채널 내 메시지 목록 페이징 조회 성공 시 200 OK 반환")
    void find_all_messages_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        UserDto authorDto = new UserDto(UUID.randomUUID(), "tester", "test@test.com", null, Role.USER, true);
        MessageDto messageDto = new MessageDto(UUID.randomUUID(), "Hello", channelId, authorDto, List.of(), Instant.now(), Instant.now());

        PageResponse<MessageDto> response = new PageResponse<>(List.of(messageDto), null, 50, false);

        given(messageService.findAllByChannelId(eq(channelId), any(), any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].content").value("Hello"));
    }

    @Test
    @DisplayName("메시지 생성 실패 시 400 Bad Request 반환 (채널 ID 누락)")
    void create_message_fail_validation() throws Exception {
        // given: channelId를 null로 세팅하여 @NotNull 검증 실패 유도
        CreateMessageRequestDto request = new CreateMessageRequestDto("Hello", null, UUID.randomUUID(), null);

        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest",
            "request.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"))
            .andExpect(jsonPath("$.details.channelId").exists()); // channelId 필드의 에러 메시지 검증
    }
}