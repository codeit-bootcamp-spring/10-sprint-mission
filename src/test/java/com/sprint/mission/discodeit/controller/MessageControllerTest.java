package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_success() throws Exception {
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("hello", channelId, authorId);
        MessageDto response = new MessageDto(
                messageId,
                Instant.parse("2026-04-01T00:00:00Z"),
                null,
                "hello",
                channelId,
                null,
                List.of()
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile attachment = new MockMultipartFile(
                "attachments",
                "note.txt",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "hello".getBytes()
        );

        given(messageService.createMessage(
                org.mockito.ArgumentMatchers.eq(authorId),
                org.mockito.ArgumentMatchers.eq(request),
                org.mockito.ArgumentMatchers.anyList()
        )).willReturn(response);

        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .file(attachment))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.content").value("hello"));
    }

    @Test
    @DisplayName("메시지 커서 페이지네이션 조회 성공")
    void findAllMessagesByChannelId_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID nextCursor = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        MessageDto message = new MessageDto(messageId, Instant.parse("2026-04-01T00:00:00Z"), null, "hello", channelId, null, List.of());
        PageResponse<MessageDto> response = new PageResponse<>(List.of(message), nextCursor, 50, true, null);

        given(messageService.findAllMessagesByChannelId(
                org.mockito.ArgumentMatchers.eq(channelId),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.any()
        )).willReturn(response);

        mockMvc.perform(get("/api/messages").param("channelId", channelId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
                .andExpect(jsonPath("$.nextCursor").value(nextCursor.toString()))
                .andExpect(jsonPath("$.hasNext").value(true));
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 실패")
    void deleteMessage_fail_notFound() throws Exception {
        UUID messageId = UUID.randomUUID();
        org.mockito.BDDMockito.willThrow(new MessageNotFoundException(
                "메세지를 찾을 수 없습니다 messageId: " + messageId,
                Map.of("messageId", messageId)
        )).given(messageService).deleteMessage(messageId);

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("MessageNotFoundException"))
                .andExpect(jsonPath("$.details.messageId").value(messageId.toString()));
    }
}
