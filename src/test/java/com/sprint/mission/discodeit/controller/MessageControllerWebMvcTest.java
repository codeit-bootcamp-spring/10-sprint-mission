package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exceptionhandler.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private MessageService messageService;

    // DiscodeitApplication에 @EnableJpaAuditing이 JpaMetamodelMappingContext를 요구함.
    // 해당 컨텍스트를 가짜로 만들어서 충돌 해결
    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("메시지 생성 성공")
    void create_message_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequestDTO req = new MessageCreateRequestDTO("hello", channelId, authorId);

        MessageDto response = new MessageDto(
            messageId,
            Instant.now(),
            Instant.now(),
            "hello",
            channelId,
            new UserDto(authorId, "tester", "tester@test.com", null, false),
            List.of()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest",
            null,
            MediaType.APPLICATION_JSON_VALUE,
            om.writeValueAsBytes(req)
        );

        given(messageService.create(isNull(), eq(req))).willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
            multipart("/api/messages")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isCreated()) // 응답의 상태가 Created이면 테스트 통과
            .andExpect(jsonPath("$.id").value(
                messageId.toString())) // Body(MessageDto)의 id 필드의 값이 messageId와 동일하면 통과
            .andExpect(jsonPath("$.content").value("hello"))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()))
            .andExpect(jsonPath("$.author.id").value(authorId.toString()))
            .andExpect(jsonPath("$.attachments").isArray());
    }

    @Test
    @DisplayName("메시지 생성 실패 - channelId 누락")
    void create_message_fail_with_invalid_request() throws Exception {
        // given
        MessageCreateRequestDTO req = new MessageCreateRequestDTO("hello", null, UUID.randomUUID());

        // 컨트롤러의 핸들러 메서드가 multipart/form-data 형식을 파라미터로 받고 dto도 requestPart 형식으로 받음
        // MockMultipartFile은 테스트에서 multipart/form-data 파트를 흉내 내는 가짜 파일 객체임.

        // requestPart는 MessageCreateRequestDto를 MultipartFile 형태로 Mocking 한 것임.
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", // 핸들러 메서드의 @RequestPart(value=) 와 동일한 이름을 가져야 한다.
            null, // 파일이 아니므로 originalFilename을 null 처리.
            MediaType.APPLICATION_JSON_VALUE, // Content-Type = Json
            om.writeValueAsBytes(req) // 객체를 json 형태로 직렬화 한다.
        );

        // when

        // mockMvc = 실제 서버 없이 컨트롤러로 HTTP 요청을 보내고 응답을 검증하는 테스트 도구
        // 가짜 HTTP로 실행하여 컨트롤러 로직을 확인
        // ResultActions = mockMvc.perform의 반환 타입
        // 실행 결과에 대해 체이닝 검증을 하게 해준다.
        ResultActions actions = mockMvc.perform(
            multipart("/api/messages") // multipart 는 기본적으로 POST 요청을 만든다. 기본 빌더가 POST로 설정되어 있음.
                .file(
                    requestPart) // multipart 요청에 파트 하나를 추가한다. MockMultipartFile로 mocking하였떤 requestPart를 추가.
                .accept(MediaType.APPLICATION_JSON) // 클라이언트가 JSON 응답을 원함.
        );

        // then 검증의 시간
        // ID가 NULL이니 FIELD_NOT_VALID 에러 코드 및 FieldNotValidException 예외 발행 예상
        // 예외 발생 시 ErrorResponse를 body로 리턴할 것임.
        actions.andExpect(status().isBadRequest()) // 응답의 status가 Bad Request면 합격
            .andExpect(jsonPath("$.code").value( // 응답 body는 json 형태라 자바에서 response.code로 바로 접근 불가능.
                "FIELD_NOT_VALID")) // 응답 body(ErrorResponse)의 code(key)의 value가 FIELD_NOT_VALID 인가?
            .andExpect(jsonPath("$.status").value(400)) // ErrorResponse 객체의 status 값이 400인가?
            .andExpect(jsonPath(
                "$.details.channelId").exists()); // ErrorResponse 객체의 details 값의 channelId가 존재하는가?
    }

    @Test
    @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
    void patch_message_fail_with_not_found() throws Exception {
        // given
        // 컨트롤러의 메시지 수정 핸들러 메서드가 메세지의 id, UpdateRequestDto를 파라미터로 받음
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequestDto req = new MessageUpdateRequestDto("updated");

        // 컨트롤러가 호출하는 messageService를 Mocking
        // 존재하지 않는 메시지의 ID를 전달하여 호출 -> MessageNotFoundException 예외 발생
        given(messageService.update(eq(messageId), eq(req)))
            .willThrow(new MessageNotFoundException(messageId));

        // when
        ResultActions actions = mockMvc.perform(
            patch("/api/messages/{messageId}",
                messageId) // 해당 uri로 messageId를 pathVariable로 전달하여 patch 요청 보냄.
                .contentType(MediaType.APPLICATION_JSON) // ContentType = json
                .content(om.writeValueAsBytes(req)) // 업데이트 요청 객체를 json 형태로 직렬화
                .accept(MediaType.APPLICATION_JSON) // 클라이언트는 Json 형태를 받음.
        );

        // then
        actions.andExpect(status().isNotFound()) // 응답의 status가 NotFound면 통과
            .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.details.id").value(messageId.toString()));
    }
}
