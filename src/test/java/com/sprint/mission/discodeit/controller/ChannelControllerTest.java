package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.advice.GlobalExceptionHandler;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequestDTO;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequestDTO;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.global.DuplicateResourceException;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelService channelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void create_public_channel_success() throws Exception {
        UUID channelId = UUID.randomUUID();

        CreatePublicChannelRequestDTO request =
                new CreatePublicChannelRequestDTO("channel", "description");

        ChannelDto response =
                new ChannelDto(channelId, ChannelType.PUBLIC, "channel", "description", List.of(), null);

        given(channelService.createPublicChannel(any())).willReturn(response);

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/channels/public/" + channelId))
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("channel"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    @DisplayName("공개 채널 생성 시 중복 이름이면 409 반환")
    void create_public_channel_fail_duplicate_name() throws Exception {
        CreatePublicChannelRequestDTO request =
                new CreatePublicChannelRequestDTO("channel", "description");

        given(channelService.createPublicChannel(any()))
                .willThrow(new DuplicateResourceException(
                        ErrorCode.CHANNEL_NAME_ALREADY_EXISTS,
                        Map.of("channelName", request.name())
                ));

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void create_private_channel_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        CreatePrivateChannelRequestDTO request =
                new CreatePrivateChannelRequestDTO(List.of(userId1, userId2));

        ChannelDto response =
                new ChannelDto(channelId, ChannelType.PRIVATE, null, null, List.of(), null);

        given(channelService.createPrivateChannel(any())).willReturn(response);

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/channels/private/" + channelId))
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("비공개 채널 생성 시 존재하지 않는 유저가 있으면 404 반환")
    void create_private_channel_fail_user_not_found() throws Exception {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        CreatePrivateChannelRequestDTO request =
                new CreatePrivateChannelRequestDTO(List.of(userId1, userId2));

        given(channelService.createPrivateChannel(any()))
                .willThrow(new com.sprint.mission.discodeit.exception.user.UserNotFoundException("존재하지 않는 사용자가 있습니다."));

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 수정 성공")
    void update_channel_success() throws Exception {
        UUID channelId = UUID.randomUUID();

        UpdateChannelRequestDTO request =
                new UpdateChannelRequestDTO("updated-channel", "updated-description");

        ChannelDto response =
                new ChannelDto(channelId, ChannelType.PUBLIC, "updated-channel", "updated-description", List.of(), null);

        given(channelService.updateChannel(eq(channelId), any())).willReturn(response);

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("updated-channel"))
                .andExpect(jsonPath("$.description").value("updated-description"));
    }

    @Test
    @DisplayName("비공개 채널 수정 시 400 반환")
    void update_channel_fail_private_channel() throws Exception {
        UUID channelId = UUID.randomUUID();

        UpdateChannelRequestDTO request =
                new UpdateChannelRequestDTO("updated-channel", "updated-description");

        given(channelService.updateChannel(eq(channelId), any()))
                .willThrow(new PrivateChannelUpdateException(channelId));

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 채널 수정 시 404 반환")
    void update_channel_fail_not_found() throws Exception {
        UUID channelId = UUID.randomUUID();

        UpdateChannelRequestDTO request =
                new UpdateChannelRequestDTO("updated-channel", "updated-description");

        given(channelService.updateChannel(eq(channelId), any()))
                .willThrow(new ChannelNotFoundException(channelId));

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_channel_success() throws Exception {
        UUID channelId = UUID.randomUUID();

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 채널 삭제 시 404 반환")
    void delete_channel_fail_not_found() throws Exception {
        UUID channelId = UUID.randomUUID();

        org.mockito.BDDMockito.willThrow(new ChannelNotFoundException(channelId))
                .given(channelService).deleteChannel(channelId);

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("userId로 채널 목록 조회 성공")
    void find_channels_by_user_id_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID channelId1 = UUID.randomUUID();
        UUID channelId2 = UUID.randomUUID();

        ChannelDto publicChannel =
                new ChannelDto(channelId1, ChannelType.PUBLIC, "public-channel", "desc", List.of(), Instant.parse("2026-03-30T08:00:00Z"));
        ChannelDto privateChannel =
                new ChannelDto(channelId2, ChannelType.PRIVATE, null, null, List.of(), null);

        given(channelService.findAllByUserId(userId))
                .willReturn(List.of(publicChannel, privateChannel));

        mockMvc.perform(get("/api/channels")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(channelId1.toString()))
                .andExpect(jsonPath("$[0].type").value("PUBLIC"))
                .andExpect(jsonPath("$[0].name").value("public-channel"))
                .andExpect(jsonPath("$[1].id").value(channelId2.toString()))
                .andExpect(jsonPath("$[1].type").value("PRIVATE"));
    }

    @Test
    @DisplayName("존재하지 않는 userId로 채널 목록 조회 시 404 반환")
    void find_channels_by_user_id_fail_not_found() throws Exception {
        UUID userId = UUID.randomUUID();

        given(channelService.findAllByUserId(userId))
                .willThrow(new com.sprint.mission.discodeit.exception.user.UserNotFoundException(userId));

        mockMvc.perform(get("/api/channels")
                        .param("userId", userId.toString()))
                .andExpect(status().isNotFound());
    }
}