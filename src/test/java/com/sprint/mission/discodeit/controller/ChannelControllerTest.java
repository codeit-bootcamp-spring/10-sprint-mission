package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("PUBLIC 채널 생성 성공")
    void createPublicChannel_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "전체 공지");
        ChannelDto response = new ChannelDto(
                channelId,
                ChannelType.PUBLIC,
                "공지",
                "전체 공지",
                List.of(),
                null
        );
        given(channelService.createPublicChannel(request)).willReturn(response);

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("공지"));
    }

    @Test
    @DisplayName("유저별 Visible 채널 조회")
    void findAllChannelsByUserId_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        ChannelDto response = new ChannelDto(channelId, ChannelType.PUBLIC, "공지", "전체 공지", List.of(), null);
        given(channelService.findAllChannelsByUserId(userId)).willReturn(List.of(response));

        mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(channelId.toString()))
                .andExpect(jsonPath("$[0].type").value("PUBLIC"));
    }

    @Test
    @DisplayName("존재하지 않는 채널 조회 실패")
    void findChannelByChannelId_fail_notFound() throws Exception {
        UUID channelId = UUID.randomUUID();
        given(channelService.findChannelByChannelId(channelId)).willThrow(new ChannelNotFoundException(
                "채널을 찾을 수 없습니다 channelId: " + channelId,
                Map.of("channelId", channelId)
        ));

        mockMvc.perform(get("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("ChannelNotFoundException"))
                .andExpect(jsonPath("$.details.channelId").value(channelId.toString()));
    }
}
