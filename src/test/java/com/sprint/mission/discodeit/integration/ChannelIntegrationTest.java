package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.ChannelEntity;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithMockUser(username = "yushi", roles = "CHANNEL_MANAGER")
public class ChannelIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtRegistry jwtRegistry;


    /*
        채널 생성
     */
    // [성공]
    @Test
    @DisplayName("공개 채널 생성 완료")
    void create_public_channel_success() throws Exception {
        // given

        // 생성할 채널
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
                "Alien",
                "To be Alien together"
        );

        // when
        mockMvc.perform(post("/api/channels/public")
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // then
        List<ChannelEntity> channels = channelRepository.findAll();
        assertEquals(createRequest.name(), channels.get(0).getName());
    }

    // [성공]
    @Test
    @DisplayName("비공개 채널 생성 완료")
    void create_private_channel_success() throws Exception {
        // given

        // 채널 참여자 목록
        UserCreateRequest firstCreateRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        UserDto firstUser = userService.create(firstCreateRequest, null);
        UserCreateRequest secondCreateRequest = new UserCreateRequest(
                "tokuno",
                "tokuno@wish.com",
                "tokuno1234"
        );
        UserDto secondUser = userService.create(secondCreateRequest, null);

        // 생성할 채널
        List<UUID> participants = List.of(firstUser.id(), secondUser.id());
        PrivateChannelCreateRequest createRequest = new PrivateChannelCreateRequest(
                participants
        );

        // when
        mockMvc.perform(post("/api/channels/private")
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // then
        List<ChannelEntity> channels = channelRepository.findAll();
        assertEquals(1, channels.size());
    }

    // [실패] 채널명 누락
    @Test
    @DisplayName("채널 생성 실패: 채널명이 누락된 경우, 400 Bad Request 반환")
    void create_channel_failure() throws Exception {
        // given

        // 생성할 채널
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
                "",
                "To be Alien together"
        );

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    /*
        채널 수정
     */
    // [성공]
    @Test
    @DisplayName("공개 채널 수정 완료")
    void update_public_channel_success() throws Exception {
        // given

        // 기존 채널 정보
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
                "Alien",
                "To be Alien together"
        );
        ChannelDto savedChannel = channelService.createPublicChannel(createRequest);

        // 수정할 채널
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
                "Meow",
                "My cat is god"
        );

        // when
        mockMvc.perform(patch("/api/channels/{channelId}", savedChannel.id())
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // then
        ChannelEntity updatedChannel = channelRepository.findById(savedChannel.id()).orElseThrow();
        assertEquals(updateRequest.newName(), updatedChannel.getName());
    }

    // [실패] 잘못된 채널 ID
    @Test
    @DisplayName("공개 채널 수정 실패: 채널 ID가 잘못된 경우, 400 Bad Request 반환")
    void update_public_channel_failure() throws Exception {
        // given
        String channelId = "channelId";

        // 수정할 채널
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
                "Meow",
                "My cat is god"
        );

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentTypeMismatchException"));
    }

    /*
        채널 삭제
     */
    // [성공]
    @Test
    @DisplayName("채널 삭제 완료")
    void delete_channel_success() throws Exception {
        // given

        // 삭제할 채널
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest(
                "Alien",
                "To be Alien together"
        );
        ChannelDto savedChannel = channelService.createPublicChannel(createRequest);

        // when
        mockMvc.perform(delete("/api/channels/{channelId}", savedChannel.id())
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER")))
                .andExpect(status().isNoContent());

        // then
        boolean exists = channelRepository.existsById(savedChannel.id());
        assertFalse(exists);
    }

    // [실패]
    @Test
    @DisplayName("채널 삭제 실패: HTTP 요청이 잘못된 경우, 405 Method Not Allowed 반환")
    void delete_channel_failure() throws Exception {
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
