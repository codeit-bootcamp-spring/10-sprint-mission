package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.advice.GlobalExceptionHandler;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("첨부파일 없이 메시지 생성 성공")
    void create_message_success_without_attachments() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        CreateMessageRequestDTO request =
                new CreateMessageRequestDTO("content", channelId, authorId);

        UserDto userDto = new UserDto(authorId, "user", "test@test.com", null, null);
        MessageDto response =
                new MessageDto(messageId, null, null, "content", channelId, userDto, List.of());

        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(messageService.createMessage(any(), any())).willReturn(response);

        mockMvc.perform(
                        multipart("/api/messages")
                                .file(messageCreateRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/messages/" + messageId))
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.username").value("user"));
    }

    @Test
    @DisplayName("첨부파일과 함께 메시지 생성 성공")
    void create_message_success_with_attachments() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        CreateMessageRequestDTO request =
                new CreateMessageRequestDTO("content", channelId, authorId);

        UserDto userDto = new UserDto(authorId, "user", "test@test.com", null, null);
        MessageDto response =
                new MessageDto(messageId, null, null, "content", channelId, userDto, List.of());

        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile attachment = new MockMultipartFile(
                "attachments",
                "file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello".getBytes()
        );

        given(messageService.createMessage(any(), any())).willReturn(response);

        mockMvc.perform(
                        multipart("/api/messages")
                                .file(messageCreateRequest)
                                .file(attachment)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    @DisplayName("존재하지 않는 작성자면 메시지 생성 시 404 반환")
    void create_message_fail_user_not_found() throws Exception {
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        CreateMessageRequestDTO request =
                new CreateMessageRequestDTO("content", channelId, authorId);

        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(messageService.createMessage(any(), any()))
                .willThrow(new UserNotFoundException(authorId));

        mockMvc.perform(
                        multipart("/api/messages")
                                .file(messageCreateRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재하지 않는 채널이면 메시지 생성 시 404 반환")
    void create_message_fail_channel_not_found() throws Exception {
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        CreateMessageRequestDTO request =
                new CreateMessageRequestDTO("content", channelId, authorId);

        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(messageService.createMessage(any(), any()))
                .willThrow(new ChannelNotFoundException(channelId));

        mockMvc.perform(
                        multipart("/api/messages")
                                .file(messageCreateRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void update_message_success() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        UpdateMessageRequestDTO request = new UpdateMessageRequestDTO("updated content");

        UserDto userDto = new UserDto(authorId, "user", "test@test.com", null, null);
        MessageDto response =
                new MessageDto(messageId, null, null, "updated content", channelId, userDto, List.of());

        given(messageService.updateMessage(eq(messageId), any())).willReturn(response);

        mockMvc.perform(
                        patch("/api/messages/{messageId}", messageId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("updated content"));
    }

    @Test
    @DisplayName("메시지 내용이 null이면 수정 시 400 반환")
    void update_message_fail_invalid_input() throws Exception {
        UUID messageId = UUID.randomUUID();

        UpdateMessageRequestDTO request = new UpdateMessageRequestDTO("updated");

        given(messageService.updateMessage(eq(messageId), any()))
                .willThrow(new InvalidInputException(
                        ErrorCode.MESSAGE_CONTENT_IS_BLANK,
                        Map.of("messageId", messageId)
                ));

        mockMvc.perform(
                        patch("/api/messages/{messageId}", messageId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 시 404 반환")
    void update_message_fail_not_found() throws Exception {
        UUID messageId = UUID.randomUUID();

        UpdateMessageRequestDTO request = new UpdateMessageRequestDTO("updated content");

        given(messageService.updateMessage(eq(messageId), any()))
                .willThrow(new MessageNotFoundException(messageId));

        mockMvc.perform(
                        patch("/api/messages/{messageId}", messageId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void delete_message_success() throws Exception {
        UUID messageId = UUID.randomUUID();

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 시 404 반환")
    void delete_message_fail_not_found() throws Exception {
        UUID messageId = UUID.randomUUID();

        willThrow(new MessageNotFoundException(messageId))
                .given(messageService).deleteMessage(messageId);

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 기준 메시지 목록 조회 성공")
    void find_all_by_channel_id_success() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        UserDto userDto = new UserDto(authorId, "user", "test@test.com", null, null);
        MessageDto messageDto =
                new MessageDto(messageId, null, null, "content", channelId, userDto, List.of());

        PageResponse<MessageDto> response =
                new PageResponse<>(List.of(messageDto), null, 10, false, null);

        given(messageService.findAllByChannelId(eq(channelId), any(), any())).willReturn(response);

        mockMvc.perform(
                        get("/api/messages")
                                .param("channelId", channelId.toString())
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
                .andExpect(jsonPath("$.content[0].content").value("content"));
    }

    @Test
    @DisplayName("존재하지 않는 채널의 메시지 목록 조회 시 404 반환")
    void find_all_by_channel_id_fail_not_found() throws Exception {
        UUID channelId = UUID.randomUUID();

        given(messageService.findAllByChannelId(eq(channelId), any(), any()))
                .willThrow(new ChannelNotFoundException(channelId));

        mockMvc.perform(
                        get("/api/messages")
                                .param("channelId", channelId.toString())
                                .param("size", "10")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("작성자 기준 메시지 목록 조회 성공")
    void find_all_by_user_id_success() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        UserDto userDto = new UserDto(authorId, "user", "test@test.com", null, null);
        MessageDto messageDto =
                new MessageDto(messageId, null, null, "content", channelId, userDto, List.of());

        PageResponse<MessageDto> response =
                new PageResponse<>(List.of(messageDto), null, 10, false, null);

        given(messageService.findAllByUserId(eq(authorId), any(), any())).willReturn(response);

        mockMvc.perform(
                        get("/api/messages/by-user")
                                .param("userId", authorId.toString())
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
                .andExpect(jsonPath("$.content[0].author.username").value("user"));
    }

    @Test
    @DisplayName("존재하지 않는 작성자의 메시지 목록 조회 시 404 반환")
    void find_all_by_user_id_fail_not_found() throws Exception {
        UUID authorId = UUID.randomUUID();

        given(messageService.findAllByUserId(eq(authorId), any(), any()))
                .willThrow(new UserNotFoundException(authorId));

        mockMvc.perform(
                        get("/api/messages/by-user")
                                .param("userId", authorId.toString())
                                .param("size", "10")
                )
                .andExpect(status().isNotFound());
    }
}