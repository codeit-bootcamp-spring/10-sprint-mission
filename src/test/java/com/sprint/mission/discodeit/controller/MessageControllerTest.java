package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc; // 가짜 HTTP 요청 객체

  @Autowired
  private ObjectMapper objectMapper; // 객체를 JSON 문자열로 변환하기 위한 객체

  @MockitoBean
  private MessageService messageService;

  @MockitoBean
  private MessageMapper messageMapper;

  @MockitoBean
  private PageResponseMapper pageResponseMapper;

  @Test
  @DisplayName("성공: 다중 첨부파일을 포함한 메시지 생성 시 201 Created를 반환한다")
  void create_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    // 1. JSON 형태의 RequestPart 생성
    MessageCreateRequest requestDto = new MessageCreateRequest("새 메시지", channelId, authorId);
    MockMultipartFile requestPart = new MockMultipartFile(
        "messageCreateRequest",
        "messageCreateRequest",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8)
    );

    // 2. 다중 첨부파일 생성
    MockMultipartFile file1 = new MockMultipartFile("attachments", "file1.png",
        MediaType.IMAGE_PNG_VALUE, "dummy1".getBytes());
    MockMultipartFile file2 = new MockMultipartFile("attachments", "file2.jpg",
        MediaType.IMAGE_JPEG_VALUE, "dummy2".getBytes());

    // 3. 서비스 및 매퍼 가짜 응답 세팅
    User mockAuthor = new User("tester", "test@test.com", "password123", null);
    Channel mockChannel = new Channel("공개 채널", "설명", ChannelType.PUBLIC);
    Message mockMessage = new Message("새 메시지", mockAuthor, mockChannel, null);

    MessageDto mockDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "새 메시지",
        channelId, null, List.of());

    given(messageService.create(eq("새 메시지"), eq(authorId), eq(channelId), any())).willReturn(
        mockMessage);
    given(messageMapper.toDto(any(Message.class))).willReturn(mockDto);

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(requestPart)
            .file(file1)
            .file(file2)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("새 메시지"));
  }

  @Test
  @DisplayName("성공: 특정 채널의 메시지를 커서 기반 페이징으로 조회하면 200 OK를 반환한다")
  void findAllByChannelId_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    User mockAuthor = new User("tester", "test@test.com", "password123", null);
    Channel mockChannel = new Channel("공개 채널", "설명", ChannelType.PUBLIC);
    Message mockMessage = new Message("페이징 메시지", mockAuthor, mockChannel, null);

    MessageDto mockDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "페이징 메시지",
        channelId, null, List.of());

    // Slice 객체 모킹
    Slice<Message> mockSlice = new SliceImpl<>(List.of(mockMessage));

    // PageResponse 객체 모킹
    PageResponse<MessageDto> mockPageResponse = new PageResponse<>(
        List.of(mockDto), // 위에서 만든 가짜 메시지 DTO 리스트
        null, // 마지막 페이지라 다음 커서가 없다고 가정
        50, // 페이지 당 데이터 수
        false, // 다음 페이지 없음
        1L // 전체 데이터 수
    );

    given(messageService.findAllByChannelId(eq(channelId), any(), any())).willReturn(mockSlice);
    given(messageMapper.toDto(any(Message.class))).willReturn(mockDto);
    given(pageResponseMapper.fromSlice(any(org.springframework.data.domain.Slice.class), any()))
        .willReturn(mockPageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("cursor", Instant.now().toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("성공: 메시지 내용을 수정하면 200 OK를 반환한다")
  void update_Success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    MessageUpdateRequest requestDto = new MessageUpdateRequest(requesterId, "수정된 내용");

    User mockAuthor = new User("tester", "test@test.com", "password123", null);
    Channel mockChannel = new Channel("공개 채널", "설명", ChannelType.PUBLIC);
    Message mockMessage = new Message("수정된 내용", mockAuthor, mockChannel, null);

    MessageDto mockDto = new MessageDto(messageId, Instant.now(), Instant.now(), "수정된 내용",
        UUID.randomUUID(), null, List.of());

    given(messageService.update(eq(messageId), eq(requesterId), eq("수정된 내용"))).willReturn(
        mockMessage);
    given(messageMapper.toDto(any(Message.class))).willReturn(mockDto);

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("수정된 내용"));
  }

  @Test
  @DisplayName("성공: 권한이 있는 유저가 메시지를 삭제하면 204 No Content를 반환한다")
  void delete_Success() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId)
            .param("requesterId", requesterId.toString()))
        .andExpect(status().isNoContent());
  }
}