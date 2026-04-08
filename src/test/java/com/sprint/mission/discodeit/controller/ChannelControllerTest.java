package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ChannelService channelService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("GET /api/channels/{id} 성공")
  void getChannel_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    ChannelDto channelDto = new ChannelDto(
        channelId,
        "public channel",
        ChannelType.PUBLIC,
        "public room",
        List.of(),
        null
    );
    given(channelService.getChannel(channelId)).willReturn(channelDto);

    // when & then
    mockMvc.perform(get("/api/channels/{id}", channelId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value("public channel"))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.description").value("public room"));
  }

  @Test
  @DisplayName("GET /api/channels/{id} 실패 - 존재하지 않는 채널")
  void getChannel_Fail_ChannelNotFound() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    given(channelService.getChannel(channelId)).willThrow(new ChannelNotFoundException(channelId));

    // when & then
    mockMvc.perform(get("/api/channels/{id}", channelId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CH001"))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
  }
}
