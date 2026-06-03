package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ChannelController.class)
public class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtRegistry jwtRegistry;

    /*
        특정 사용자가 참여하고 있는 목록 조회
     */
    // [성공]
    @Test
    @DisplayName("특정 사용자가 참여하고 있는 목록 조회 완료")
    void find_all_by_user_id_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        ChannelDto firstChannel = ChannelDto.builder()
                .id(UUID.randomUUID())
                .name("Alien")
                .description("To be Alien together")
                .build();
        ChannelDto secondChannel = ChannelDto.builder()
                .id(UUID.randomUUID())
                .name("Meow")
                .description("My cat is god")
                .build();

        List<ChannelDto> channels = List.of(firstChannel, secondChannel);
        given(channelService.findAllByUserId(userId)).willReturn(channels);

        // when & then
        mockMvc.perform(get("/api/channels")
                        .with(user("yushi").roles("USER"))
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alien"));
    }

    // [실패] 파라미터 누락
    @Test
    @DisplayName("특정 사용자가 참여하고 있는 목록 조회 실패: 특정 사용자 ID가 누락된 경우, 400 Bad Request 반환")
    void find_all_by_user_id_failure() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/api/channels")
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MissingServletRequestParameterException"));
    }

    /*
        채널 생성
     */
    // [성공]
    @Test
    @DisplayName("공개 채널 생성 완료")
    void create_public_channel_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "Alien",
                "To be Alien together"
        );

        // 생성할 공개 채널
        ChannelDto responseDto = ChannelDto.builder()
                .id(channelId)
                .type(ChannelType.PUBLIC)
                .name(request.name())
                .description(request.description())
                .lastMessageAt(Instant.now())
                .build();
        given(channelService.createPublicChannel(request)).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("Alien"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    // [실패] 채널명 공백
    @Test
    @DisplayName("공개 채널 생성 실패: 채널명이 누락될 경우, 400 Bad Request 반환")
    void create_public_channel_failure() throws Exception {
        // given
        String json = """
                {
                    "description": "To be Alien together"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/channels/public")
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    // [성공]
    @Test
    @DisplayName("비공개 채널 생성 완료")
    void create_private_channel_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        // 비공개 채널 참여자 목록
        List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        // 생성할 공개 채널
        ChannelDto responseDto = ChannelDto.builder()
                .id(channelId)
                .type(ChannelType.PRIVATE)
                .lastMessageAt(Instant.now())
                .build();
        given(channelService.createPrivateChannel(any(PrivateChannelCreateRequest.class))).willReturn(responseDto);


        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()));
    }

    // [실패] 참여자 목록 누락
    @Test
    @DisplayName("비공개 채널 생성 실패: 참여자 목록이 누락될 경우, 400 Bad Request 반환")
    void create_private_channel_failure() throws Exception {
        // given

        // 비공개 채널 참여자
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(null);

        // When & Then
        mockMvc.perform(post("/api/channels/private")
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    /*
        공개 채널 수정
     */
    // [성공]
    @Test
    @DisplayName("공개 채널 수정 완료")
    void update_public_channel_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "Meow",
                "My cat is god"
        );

        // 수정할 채널 객체
        ChannelDto response = ChannelDto.builder()
                .id(channelId)
                .type(ChannelType.PUBLIC)
                .name(request.newName())
                .description(request.newDescription())
                .lastMessageAt(Instant.now())
                .build();
        given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Meow"));
    }

    // [실패] 잘못된 채널 ID 전달
    @Test
    @DisplayName("공개 채널 수정 실패: 채널 ID가 잘못된 경우, 400 Bad Request 반환")
    void update_public_channel_failure() throws Exception {
        // given
        String channelId = "channelId";
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                "Meow",
                "My cat is god"
        );

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentTypeMismatchException"));
    }

    /*
        채널 삭제
     */
    // [성공]
    @Test
    @DisplayName("채널 삭제 완료")
    void delete_public_channel_success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId)
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER")))
                .andExpect(status().isNoContent());
    }

    // [실패]류 HTTP 메서드 오류
    @Test
    @DisplayName("채널 삭제 실패: HTTP 메서드가 잘못된 경우, 405 Method Not Allowed 반환")
    void delete_public_channel_failure() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        // when & then
        mockMvc.perform(post("/api/channels/{channelId}", channelId)
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.exceptionType").value("HttpRequestMethodNotSupportedException"));
    }
}
