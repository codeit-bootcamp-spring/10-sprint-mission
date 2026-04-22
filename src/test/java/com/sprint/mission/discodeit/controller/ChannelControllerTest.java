package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ChannelControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ChannelService channelService;

    @Test
    @DisplayName("public 채널 생성 성공 시 201 Created 반환")
    void create_public_channel_success() throws Exception {
        CreatePublicChannelRequestDto request = new CreatePublicChannelRequestDto("PublicChat", "Desc");
        ChannelDto response = new ChannelDto(UUID.randomUUID(), request.name(), request.description(), ChannelType.PUBLIC, Instant.now(), List.of());

        given(channelService.createPublic(any())).willReturn(response);

        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("PublicChat"));
    }

    @Test
    @DisplayName("private 채널 생성 성공 시 201 Created 반환")
    void create_private_channel_success() throws Exception {
        CreatePrivateChannelRequestDto request = new CreatePrivateChannelRequestDto(List.of(UUID.randomUUID()));
        ChannelDto response = new ChannelDto(UUID.randomUUID(), "Private", null, ChannelType.PRIVATE, Instant.now(), List.of());

        given(channelService.createPrivate(any())).willReturn(response);

        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("채널 목록 조회 성공 시 200 OK 반환")
    void find_all_channels_success() throws Exception {
        UUID userId = UUID.randomUUID();
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), "Chat", null, ChannelType.PUBLIC, Instant.now(), List.of());

        given(channelService.findAllByUserId(userId)).willReturn(List.of(channelDto));

        mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Chat"));
    }

    @Test
    @DisplayName("채널 정보 수정 성공 시 200 OK 반환")
    void update_channel_success() throws Exception {
        UUID channelId = UUID.randomUUID();
        UpdateChannelRequestDto request = new UpdateChannelRequestDto("UpdatedName", "UpdatedDesc");
        ChannelDto response = new ChannelDto(channelId, request.newName(), request.newDescription(), ChannelType.PUBLIC, Instant.now(), List.of());

        given(channelService.update(eq(channelId), any())).willReturn(response);

        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdatedName"));
    }

    @Test
    @DisplayName("채널 삭제 성공 시 204 No Content 반환")
    void delete_channel_success() throws Exception {
        UUID channelId = UUID.randomUUID();

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("채널 삭제 실패 시 404 Not Found 반환 (존재하지 않는 채널)")
    void delete_channel_fail_not_found() throws Exception {
        // given: 서비스 레이어의 delete 메서드가 호출되면 NoSuchElementException을 던지도록 가짜 대본 설정
        UUID channelId = UUID.randomUUID();
        org.mockito.Mockito.doThrow(new java.util.NoSuchElementException("채널을 찾을 수 없습니다."))
            .when(channelService).delete(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()) // HTTP 404 검증
            .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND")); // GlobalExceptionHandler의 404 처리 코드 검증
    }
}