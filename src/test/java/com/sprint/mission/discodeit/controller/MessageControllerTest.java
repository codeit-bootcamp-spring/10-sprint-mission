package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.response.MessageDTO;
import com.sprint.mission.discodeit.dto.page.PageResponse;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * @WebMvcTest(MessageController.class)
 * - MessageController만 포함한 웹 레이어를 로드한다.
 * - MessageService는 로드되지 않으므로 @MockitoBean으로 대체한다.
 */
@WebMvcTest(MessageController.class)
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    private UUID messageId;
    private UUID channelId;
    private UUID authorId;
    private MessageDTO messageDTO;

    /*
     * @BeforeEach
     * - 각 테스트에서 공통으로 사용하는 UUID와 MessageDTO를 미리 세팅한다.
     * - MessageDTO는 응답 DTO이므로 서비스 Mock의 반환값으로 사용된다.
     */
    @BeforeEach
    void setUp() {
        messageId = UUID.randomUUID();
        channelId = UUID.randomUUID();
        authorId  = UUID.randomUUID();

        // 작성자 DTO (MessageDTO 안에 중첩되어 있음)
        UserDTO authorDTO = new UserDTO(authorId, "승주", "jsj@naver.com", null, false);

        messageDTO = new MessageDTO(
                messageId,
                Instant.now(),
                Instant.now(),
                "안녕하세요!",
                channelId,
                authorDTO,
                List.of()   // 첨부파일 없음
        );
    }

    // ===================== postMessage =====================

    /*
     * 메시지 생성 - 성공 케이스
     *
     * MessageController.postMessage는 multipart/form-data 방식으로 요청을 받는다.
     *   - "messageCreateRequest" 파트: JSON 데이터 (content, channelId, authorId)
     *   - "attachments" 파트: 선택적 파일 목록 (required=false)
     *
     * attachments 파트를 보내지 않으면 서비스에 빈 리스트가 전달된다.
     * (Controller 내부에서 Optional.ofNullable(attachments).orElse(new ArrayList<>())로 처리)
     */
    @Test
    @DisplayName("메시지 생성 성공 - 201 Created + MessageDTO 반환")
    void postMessage_성공() throws Exception {
        // given
        MessageCreateRequest request = new MessageCreateRequest("안녕하세요!", channelId, authorId);
        given(messageService.create(any(), any())).willReturn(messageDTO);

        // JSON 데이터를 "messageCreateRequest" 파트로 감싸서 전송
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("안녕하세요!"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()));
    }

    /*
     * 메시지 생성 실패 - content가 빈 값인 경우
     *
     * MessageCreateRequest.content에 @NotBlank가 붙어 있다.
     * 빈 문자열("")을 보내면 Validation이 실패하고 400 Bad Request로 응답한다.
     */
    @Test
    @DisplayName("메시지 생성 실패 - content가 빈 값이면 400 Bad Request")
    void postMessage_실패_내용없음() throws Exception {
        // given: @NotBlank 위반
        MessageCreateRequest request = new MessageCreateRequest("", channelId, authorId);

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.content").exists());

        // Validation 실패로 서비스가 호출되면 안 된다
        then(messageService).should(never()).create(any(), any());
    }

    /*
     * 메시지 생성 실패 - channelId가 null인 경우
     *
     * MessageCreateRequest.channelId에 @NotNull이 붙어 있다.
     */
    @Test
    @DisplayName("메시지 생성 실패 - channelId가 null이면 400 Bad Request")
    void postMessage_실패_채널없음() throws Exception {
        // given: @NotNull 위반 - channelId를 null로 설정
        MessageCreateRequest request = new MessageCreateRequest("안녕하세요!", null, authorId);

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.channelId").exists());

        then(messageService).should(never()).create(any(), any());
    }

    /*
     * 메시지 생성 실패 - 존재하지 않는 채널
     *
     * Validation은 통과하지만, 서비스 내부에서 해당 channelId의 채널이 없으면
     * ChannelNotFoundException을 던진다.
     * GlobalExceptionHandler가 이를 404 Not Found로 처리한다.
     */
    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 채널이면 404 Not Found")
    void postMessage_실패_없는채널() throws Exception {
        // given
        MessageCreateRequest request = new MessageCreateRequest("안녕하세요!", channelId, authorId);
        given(messageService.create(any(), any())).willThrow(new ChannelNotFoundException(channelId));

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart))
                .andExpect(status().isNotFound());
    }

    // ===================== updateMessage =====================

    /*
     * 메시지 수정
     * PATCH /api/messages/{messageId}
     *
     * MessageUpdateRequest는 일반 JSON body로 전송한다.
     * (Controller에서 @RequestBody를 사용하므로 contentType(APPLICATION_JSON) 필요)
     *
     * multipart와의 차이:
     *   - multipart: 파일과 JSON을 함께 전송할 때 사용
     *   - @RequestBody: JSON만 전송할 때 사용, 더 단순한 방식
     */
    @Test
    @DisplayName("메시지 수정 성공 - 200 OK + 수정된 MessageDTO 반환")
    void updateMessage_성공() throws Exception {
        // given
        MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용입니다.");
        MessageDTO updatedDTO = new MessageDTO(
                messageId, Instant.now(), Instant.now(),
                "수정된 내용입니다.", channelId,
                new UserDTO(authorId, "승주", "jsj@naver.com", null, false),
                List.of()
        );
        given(messageService.update(eq(messageId), any())).willReturn(updatedDTO);

        // when, then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 내용입니다."));
    }

    @Test
    @DisplayName("메시지 수정 실패 - 존재하지 않는 messageId면 404 Not Found")
    void updateMessage_실패_없는메시지() throws Exception {
        // given
        MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용입니다.");
        given(messageService.update(eq(messageId), any())).willThrow(new MessageNotFoundException(messageId));

        // when, then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 수정 실패 - newContent가 빈 값이면 400 Bad Request")
    void updateMessage_실패_내용없음() throws Exception {
        // given: @NotBlank 위반
        MessageUpdateRequest request = new MessageUpdateRequest("");

        // when, then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.newContent").exists());

        then(messageService).should(never()).update(any(), any());
    }

    // ===================== deleteMessage =====================

    @Test
    @DisplayName("메시지 삭제 성공 - 204 No Content")
    void deleteMessage_성공() throws Exception {
        // given
        // void 메서드는 willDoNothing()으로 아무 일도 안 함을 명시한다.
        willDoNothing().given(messageService).deleteMessage(messageId);

        // when, then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());

        // deleteMessage가 정확히 1번 호출됐는지 검증
        then(messageService).should().deleteMessage(messageId);
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 존재하지 않는 messageId면 404 Not Found")
    void deleteMessage_실패_없는메시지() throws Exception {
        // given
        // void 메서드에 예외 설정: willThrow().given() 순서 사용
        // 이유: given(service.voidMethod()).willThrow()는 void를 given()에 넘길 수 없어서 컴파일 에러 발생
        willThrow(new MessageNotFoundException(messageId)).given(messageService).deleteMessage(messageId);

        // when, then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNotFound());
    }

    // ===================== getAllMessages =====================

    /*
     * 메시지 목록 조회 - 커서 기반 페이징
     * GET /api/messages?channelId=...&cursor=...
     *
     * 커서 기반 페이징(Cursor-based Pagination)이란?
     *   - 일반적인 페이징은 "몇 번째 페이지를 주세요" (offset 방식)
     *   - 커서 기반은 "이 시점 이전의 메시지를 주세요" (cursor 방식)
     *   - 채팅처럼 새 메시지가 계속 추가되는 경우 cursor 방식이 더 적합하다.
     *   - cursor가 없으면 가장 최신 메시지부터 조회한다.
     *
     * PageResponse<MessageDTO>:
     *   - content: 실제 메시지 목록
     *   - nextCursor: 다음 페이지 요청 시 사용할 커서 값
     *   - hasNext: 다음 페이지 존재 여부
     */
    @Test
    @DisplayName("메시지 목록 조회 성공 - 200 OK + 페이지 응답 반환")
    void getAllMessages_성공() throws Exception {
        // given
        // cursor 없이 요청 → 가장 최신 메시지부터 조회
        PageResponse<MessageDTO> pageResponse = new PageResponse<>(
                List.of(messageDTO),
                null,   // nextCursor: 다음 페이지 없음
                1,
                false,  // hasNext: false
                1L
        );
        given(messageService.findMessagesByChannel(eq(channelId), isNull(), any())).willReturn(pageResponse);

        // when, then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString()))  // 쿼리 파라미터로 channelId 전달
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))         // content 배열 크기 검증
                .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("메시지 목록 조회 성공 - 메시지가 없으면 빈 리스트 반환")
    void getAllMessages_빈리스트() throws Exception {
        // given
        PageResponse<MessageDTO> emptyResponse = new PageResponse<>(
                List.of(),
                null,
                0,
                false,
                0L
        );
        given(messageService.findMessagesByChannel(eq(channelId), isNull(), any())).willReturn(emptyResponse);

        // when, then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("메시지 목록 조회 실패 - 존재하지 않는 channelId면 404 Not Found")
    void getAllMessages_실패_없는채널() throws Exception {
        // given
        given(messageService.findMessagesByChannel(eq(channelId), isNull(), any()))
                .willThrow(new ChannelNotFoundException(channelId));

        // when, then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString()))
                .andExpect(status().isNotFound());
    }
}
