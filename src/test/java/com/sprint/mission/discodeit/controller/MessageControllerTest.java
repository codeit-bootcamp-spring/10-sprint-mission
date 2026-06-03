package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.message.PageResponse;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtRegistry jwtRegistry;


    /*
        메시지 생성
     */
    // [성공]
    @Test
    @DisplayName("메시지 생성 완료")
    void create_message_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(
                "LUV ME HATE ME",
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        // JSON 파일화
        String json = objectMapper.writeValueAsString(request);

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile attachmentPart = new MockMultipartFile(
                "attachments",
                "alien.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes()
        );

        // 생성할 메시지
        MessageDto response = MessageDto.builder()
                .id(messageId)
                .content(request.content())
                .build();
        given(messageService.create(any(MessageCreateRequest.class), any())).willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .file(attachmentPart)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf())
                    .with(user("yushi").roles("USER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("LUV ME HATE ME"));
    }

    // [실패] 메시지 내용 공백
    @Test
    @DisplayName("메시지 생성 실패: 채널 ID가 누락된 경우, 400 Bad Request 반환")
    void create_message_failure() throws Exception {
        // given
        MessageCreateRequest request = new MessageCreateRequest(
                "LUV ME HATE ME",
                UUID.randomUUID(),
                null
        );
        String json = objectMapper.writeValueAsString(request);

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    /*
        특정 채널의 메시지 목록 조회
     */
    // [성공]
    @Test
    @DisplayName("특정 채널의 메시지 목록 조회 완료")
    void find_all_message_by_channel_id_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        MessageDto firstMessage = MessageDto.builder()
                .id(UUID.randomUUID())
                .content("LUV ME HATE ME")
                .build();
        MessageDto secondMessage = MessageDto.builder()
                .id(UUID.randomUUID())
                .content("KILL ME KILL ME")
                .build();

        PageResponse<MessageDto> messages = PageResponse.<MessageDto>builder()
                .content(List.of(firstMessage, secondMessage))
                .nextCursor(Instant.now())
                .size(10)
                .hasNext(true)
                .build();
        given(messageService.findAllByChannelId(eq(channelId), any(), anyInt())).willReturn(messages);

        // when & then
        mockMvc.perform(get("/api/messages")
                    .param("channelId", channelId.toString())
                    .with(csrf())
                    .with(user("yushi").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].content").value("LUV ME HATE ME"))
                .andExpect(jsonPath("$.content[1].content").value("KILL ME KILL ME"))
                .andExpect(jsonPath("$.hasNext").value(true));
    }

    // [실패] 파라미터 누락
    @Test
    @DisplayName("특정 채널의 메시지 목록 조회 실패: 특정 채널 ID가 누락된 경우, 400 Bad Request 반환")
    void find_all_message_by_channel_id_failure() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/api/messages")
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MissingServletRequestParameterException"));
    }


    /*
        메시지 수정
     */
    // [성공]
    @Test
    @DisplayName("메시지 수정 완료")
    void update_message_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("KILL ME KILL ME");

        // 수정할 메시지
        MessageDto response = MessageDto.builder()
                .id(messageId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .content(request.newContent())
                .channelId(UUID.randomUUID())
                .build();
        given(messageService.update(eq(messageId), any(MessageUpdateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("KILL ME KILL ME"));

    }

    // [실패] 잘못된 메시지 ID 전달
    @Test
    @DisplayName("메시지 수정 실패: 메시지 ID가 잘못된 경우, 400 Bad Request 반환")
    void update_message_failure() throws Exception {
        // given
        String messageId = "messageId";
        MessageUpdateRequest request = new MessageUpdateRequest("HATE ME HATE ME");

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentTypeMismatchException"));
    }

    /*
        메시지 삭제
     */
    // [성공]
    @Test
    @DisplayName("메시지 삭제 완료")
    void delete_message_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isNoContent());
    }

    // [실패] HTTP 메서드 오류
    @Test
    @DisplayName("메시지 삭제 실패: HTTP 메서드가 잘못된 경우, 405 Method Not Allowed 반환")
    void delete_message_failure() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        // when & then
        mockMvc.perform(post("/api/messages/{messageId}", messageId)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.exceptionType").value("HttpRequestMethodNotSupportedException"));
    }
}
