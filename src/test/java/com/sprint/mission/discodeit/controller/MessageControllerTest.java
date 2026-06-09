package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.PageResponse;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.InvalidMessageException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(
    controllers = MessageController.class,
    // 테스트할 때 검증 필터를 스캔 대상에서 제외
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class
    )
)
@ActiveProfiles("test")
@Import({GlobalExceptionHandler.class})
@Tag("unit")
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;
  @MockitoBean
  private BinaryContentMapper binaryContentMapper;

  @Test
  @DisplayName("성공: 첨부파일과 함께 메세지 전송 성공(201 created)")
  void sendMessageWithAttachmentsSuccess() throws Exception {
    //given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("testContent", channelId, userId);
    UserDto userDto = new UserDto(userId, "test", "test@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    BinaryContentDto attachmentDto1 = new BinaryContentDto(UUID.randomUUID(), "myImage", 50L,
        "jpg", BinaryContentStatus.PROCESSING);
    BinaryContentDto attachmentDto2 = new BinaryContentDto(UUID.randomUUID(), "myTxt", 50L, "txt",
        BinaryContentStatus.PROCESSING);
    List<BinaryContentDto> attachments = List.of(attachmentDto1, attachmentDto2);
    MessageDto messageDto = new MessageDto(messageId, request.content(), request.channelId(),
        userDto,
        attachments, Instant.now(), Instant.now());

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // 첨부파일 생성
    // 첨부파일 1
    MockMultipartFile attachmentsPart1 = new MockMultipartFile(
        "attachments",
        "myImage",
        MediaType.IMAGE_JPEG_VALUE,
        "test-image-content".getBytes()
    );
    // 첨부파일 2
    MockMultipartFile attachmentsPart2 = new MockMultipartFile(
        "attachments",
        "myTxt.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "text-content".getBytes()
    );

    given(messageService.create(any(), any())).willReturn(messageDto);

    //when & then
    mockMvc.perform(multipart("/api/messages")
            .file(userPart)
            .file(attachmentsPart1)
            .file(attachmentsPart2)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.content").value(request.content()))
        .andExpect(jsonPath("$.channelId").value(request.channelId().toString()))
        .andExpect(jsonPath("$.author.id").value(userId.toString()))
        .andExpect(jsonPath("$.attachments.size()").value(2));
  }

  @Test
  @DisplayName("실패: 빈 메세지 전송 실패(400 bad request)")
  void sendMessageWithNoContentFailure() throws Exception {
    //given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest("", channelId, userId);

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    given(messageService.create(any(), any())).willThrow(new InvalidMessageException());

    //when & then
    mockMvc.perform(multipart("/api/messages")
            .file(userPart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.exceptionType").value(InvalidMessageException.class.getSimpleName()));
  }

  @Test
  @DisplayName("성공: 메세지 내용 변경 성공(200 ok)")
  void updateMessageContentSuccess() throws Exception {
    //given
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");
    UserDto userDto = new UserDto(userId, "test", "test@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    BinaryContentDto attachmentDto1 = new BinaryContentDto(UUID.randomUUID(), "myImage", 50L,
        "jpg", BinaryContentStatus.PROCESSING);
    BinaryContentDto attachmentDto2 = new BinaryContentDto(UUID.randomUUID(), "myTxt", 50L, "txt",
        BinaryContentStatus.PROCESSING);
    List<BinaryContentDto> attachments = List.of(attachmentDto1, attachmentDto2);
    MessageDto messageDto = new MessageDto(messageId, request.newContent(), channelId,
        userDto,
        attachments, Instant.now(), Instant.now());

    given(messageService.update(eq(messageId), any())).willReturn(messageDto);

    //DTO > JSON
    String body = objectMapper.writeValueAsString(request);

    //when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value(request.newContent()));
  }

  @Test
  @DisplayName("실패: 유효하지 않은 메세지 아이디에 대해 메세지 내용 변경 실패(404 not found)")
  void updateMessageContentWithNotExistedMessageIdFailure() throws Exception {
    //given
    UUID wrongMessageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");

    given(messageService.update(eq(wrongMessageId), any())).willThrow(
        new MessageNotFoundException());

    //DTO > JSON
    String body = objectMapper.writeValueAsString(request);

    //when & then
    mockMvc.perform(patch("/api/messages/{messageId}", wrongMessageId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.exceptionType").value(MessageNotFoundException.class.getSimpleName()));
  }

  @Test
  @DisplayName("성공: 메세지 삭제 성공(204 No content)")
  void deleteMessageSuccess() throws Exception {
    //given
    UUID messageId = UUID.randomUUID();

    //when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    then(messageService).should(times(1)).delete(messageId);
  }

  @Test
  @DisplayName("실패: 유효하지 않은 메세지 아이디로 사용자 삭제 실패(404 Not found)")
  void deleteMessageByWrongUserIdFailure() throws Exception {
    //given
    UUID wrongMessageId = UUID.randomUUID();
    willThrow(new MessageNotFoundException()).given(messageService).delete(eq(wrongMessageId));

    //when & then
    mockMvc.perform(delete("/api/messages/{messageId}", wrongMessageId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.exceptionType").value(MessageNotFoundException.class.getSimpleName()));

  }

  @Test
  @DisplayName("성공: 채널 아이디로 페이지네이션이 적용된 메세지 목록 조회 성공")
  void findMessagesByChannelIdSuccess() throws Exception {
    //given
    UUID u1Id = UUID.randomUUID();
    UUID u2Id = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UserDto userDto1 = new UserDto(u1Id, "test1", "test1@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    UserDto userDto2 = new UserDto(u2Id, "test2", "test2@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    Instant cursor = Instant.now();
    MessageDto messageDto1 = new MessageDto(UUID.randomUUID(), "m1", channelId, userDto1, List.of(),
        Instant.now(), Instant.now());
    MessageDto messageDto2 = new MessageDto(UUID.randomUUID(), "m2", channelId, userDto2, List.of(),
        Instant.now(), Instant.now());
    List<MessageDto> messages = List.of(messageDto1, messageDto2);
    PageResponse<MessageDto> pageResponse = new PageResponse<>(messages, null, 2, false, null);

    given(messageService.findAllByChannelId(eq(channelId), any(Pageable.class), any(Instant.class)))
        .willReturn(pageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("size", "2")
            .param("sort", "createdAt,desc")
            .param("cursor", cursor.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(2));
  }

  @Test
  @DisplayName("성공: 페이지네이션 기본 값으로 채널 아이디로 페이지네이션이 적용된 메세지 목록 조회 성공")
  void findMessagesByChannelIdWithDefaultPaginationSuccess() throws Exception {
    //given
    UUID u1Id = UUID.randomUUID();
    UUID u2Id = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UserDto userDto1 = new UserDto(u1Id, "test1", "test1@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    UserDto userDto2 = new UserDto(u2Id, "test2", "test2@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    Instant cursor = Instant.now();
    MessageDto messageDto1 = new MessageDto(UUID.randomUUID(), "m1", channelId, userDto1, List.of(),
        Instant.now(), Instant.now());
    MessageDto messageDto2 = new MessageDto(UUID.randomUUID(), "m2", channelId, userDto2, List.of(),
        Instant.now(), Instant.now());
    List<MessageDto> messages = List.of(messageDto1, messageDto2);
    PageResponse<MessageDto> pageResponse = new PageResponse<>(messages, null, 2, false, null);

    given(messageService.findAllByChannelId(eq(channelId), any(Pageable.class), any()))
        .willReturn(pageResponse);

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(2));
  }
}