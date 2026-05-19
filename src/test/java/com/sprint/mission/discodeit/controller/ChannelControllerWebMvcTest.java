package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.SecurityConfig;
import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exceptionhandler.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ChannelController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class ChannelControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockBean
    ChannelService channelService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void public_channel_create_success() throws Exception {
        // given
        PublicChannelCreateDTO req = new PublicChannelCreateDTO("abc", "abcdef");

        given(channelService.createPublicChannel(ArgumentMatchers.eq(req)))
            .willReturn(new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                "abc",
                "abcdef",
                List.of(),
                null
            ));

        // when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(om.writeValueAsBytes(req))
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        response.andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PUBLIC"))
            .andExpect(jsonPath("$.name").value("abc"))
            .andExpect(jsonPath("$.description").value("abcdef"));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void private_channel_create_success() throws Exception {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        PrivateChannelCreateDTO req = new PrivateChannelCreateDTO(List.of(userId1, userId2));

        given(channelService.createPrivateChannel(ArgumentMatchers.eq(req)))
            .willReturn(new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PRIVATE,
                null,
                null,
                List.of(),
                null
            ));

        // when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(om.writeValueAsBytes(req))
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        response.andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("비공개 채널 생성 실패 - users 누락")
    void private_channel_create_fail_with_missing_users() throws Exception {
        // given
        String invalidJson = "{}";

        // when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(invalidJson)
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        response.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("FIELD_NOT_VALID"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.details.users").exists());
    }
}
