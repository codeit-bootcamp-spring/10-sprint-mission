package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper obj;

  @MockitoBean
  private ChannelService channelService;

  @Test
  @DisplayName("PUBLIC 채널 생성 요청을 처리하여 응답을 반환할 수 있어야 한다.")
  void should_return_response_when_create_public_channel() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개 채널", "공개 채널입니다.");
    ChannelDto expectedResponse = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "공개 채널",
        "공개 채널입니다.", null, null);
    String body = obj.writeValueAsString(request);

    given(channelService.createPublic(any(PublicChannelCreateRequest.class))).willReturn(
        expectedResponse);

    // when
    ResultActions actions = mockMvc.perform(
        post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body)
    );

    // then
    actions.andExpect(status().isCreated())
        .andDo(print())
        .andExpect(jsonPath("$.name").value("공개 채널"))
        .andExpect(jsonPath("$.description").value("공개 채널입니다."))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("존재하지 않는 채널을 삭제하려고 하면 404를 반환해야 한다.")
  void should_return_404_when_delete_not_exist_channel() throws Exception {
    // given
    doThrow(new ChannelNotFoundException())
        .when(channelService).delete(any(UUID.class));

    // when
    ResultActions actions = mockMvc.perform(
        delete("/api/channels/" + UUID.randomUUID())
    );

    // then
    actions.andExpect(status().isNotFound())
        .andDo(print())
        .andExpect(jsonPath("$.message").exists());
  }
}