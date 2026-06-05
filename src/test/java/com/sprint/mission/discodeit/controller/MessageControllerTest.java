package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper obj;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("메시지 생성 요청을 처리하여 응답을 반환할 수 있어야 한다.")
  void should_return_response_when_create_message() throws Exception {
    // given
    MessageCreateRequest request = new MessageCreateRequest("메시지 생성 테스트", UUID.randomUUID(),
        UUID.randomUUID());

    BinaryContentDto profileDto = new BinaryContentDto(
        UUID.randomUUID(),
        "test.png",
        1024L,
        "image/png",
        BinaryContentStatus.PROCESSING
    );

    UserDto userDto = new UserDto(
        UUID.randomUUID(),
        "김코딩",
        "hello@hello.com",
        null,
        true,
        Role.USER
    );

    MessageDto expectedResponse = new MessageDto(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        "메시지 생성 테스트",
        UUID.randomUUID(),
        userDto,
        List.of(profileDto, profileDto)
    );

    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        obj.writeValueAsBytes(request)
    );

    MockMultipartFile attachmentPart = new MockMultipartFile(
        "attachments",
        "test.png",
        MediaType.IMAGE_PNG_VALUE,
        "가짜 이미지 데이터".getBytes()
    );

    given(messageService.create(any(MessageCreateRequest.class), anyList())).willReturn(
        expectedResponse);

    // when
    ResultActions actions = mockMvc.perform(
        multipart("/api/messages")
            .file(messagePart)
            .file(attachmentPart)
            .file(attachmentPart)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    actions.andExpect(status().isCreated())
        .andDo(print())
        .andExpect(jsonPath("$.content").value("메시지 생성 테스트"))
        .andExpect(jsonPath("$.attachments").exists());
  }

  @Test
  @DisplayName("존재하지 않는 메시지를 삭제하려고 하면 404를 반환해야 한다.")
  void should_return_404_when_delete_not_exist_message() throws Exception {
    // given
    doThrow(new MessageNotFoundException())
        .when(messageService).delete(any(UUID.class));

    // when
    ResultActions actions = mockMvc.perform(
        delete("/api/messages/" + UUID.randomUUID())
    );

    // then
    actions.andExpect(status().isNotFound())
        .andDo(print())
        .andExpect(jsonPath("$.message").exists());
  }
}