package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MessageService messageService;

    @Test
    @DisplayName("메시지 생성: 텍스트와 파일 1개를 보내면 201 Created를 반환해야 한다")
    void postMessage_success() throws Exception {
        // given
        MockMultipartFile attachmentPart = new MockMultipartFile(
                "attachments", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello File".getBytes()
        );
        BinaryContentDto attachment = new BinaryContentDto(
                UUID.randomUUID(),
                attachmentPart.getSize(),
                attachmentPart.getOriginalFilename(),
                attachmentPart.getContentType()
        );

        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UserDto author = new UserDto(authorId, "김현재","fred@naver.com", null ,true);

        MessageCreateRequest requestDto = new MessageCreateRequest(authorId, channelId, "안녕!");
        MessageDto responseDto = new MessageDto(UUID.randomUUID(),
                author,
                channelId,
                Instant.now(),
                Instant.now(),
                List.of(attachment),
                "안녕");

        given(messageService.create(any(), any())).willReturn(responseDto);

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .file(attachmentPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attachments.size()").value(1))
                .andExpect(jsonPath("$.attachments[0].fileName").value("hello.txt"))
                .andExpect(jsonPath("$.content").value("안녕"));
    }

    @Test
    @DisplayName("채널 내 메시지 조회: channelId와 페이징 정보로 200 OK와 PageResponse를 반환해야 한다")
    void findAllByChannelId_success() throws Exception {
        // given
        UUID targetChannelId = UUID.randomUUID();

        MessageDto msg1 = new MessageDto(UUID.randomUUID(),
                new UserDto(UUID.randomUUID(), "김현재","fred@naver.com", null,true),
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                null,
                "안녕");
        PageResponse<MessageDto> pageResponse = new PageResponse<>(
                List.of(msg1),
                null,
                1,
                false,
                1L
        );

        given(messageService.findAllMessagesByChannelId(eq(targetChannelId), any(), any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", targetChannelId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("안녕"))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("메시지 업데이트: 순정 JSON PATCH 요청 시 200 OK를 반환해야 한다")
    void updateMessage_success() throws Exception {
        // given
        UUID targetMessageId = UUID.randomUUID();

        MessageUpdateRequest requestDto = new MessageUpdateRequest("수정된 메시지");
        MessageDto responseDto = new MessageDto(UUID.randomUUID(),
                new UserDto(UUID.randomUUID(), "김현재","fred@naver.com", null,true),
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                null,
                requestDto.getNewContent());

        given(messageService.update(eq(targetMessageId), any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(patch("/api/messages/{id}", targetMessageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 메시지"));
    }

    @Test
    @DisplayName("메시지 삭제: 메시지 ID를 보내면 성공적으로 삭제하고 204 No Content를 반환해야 한다")
    void deleteMessage_success() throws Exception {
        // given
        UUID targetMessageId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/messages/{id}", targetMessageId))
                .andExpect(status().isNoContent());
    }
}