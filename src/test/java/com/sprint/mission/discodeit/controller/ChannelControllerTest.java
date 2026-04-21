package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.type.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    ChannelService channelService;

    @Test
    @DisplayName("공용 채널 생성: 올바른 요청 시 201 Created와 채널 정보를 반환해야 한다")
    void postPublicChannel_success() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("자유게시판", "누구나 참여 가능");
        ChannelDto response = new ChannelDto(UUID.randomUUID(),
                "자유게시판",
                "누구나 참여 가능",
                null,
                Instant.now(),
                ChannelType.PUBLIC);

        given(channelService.createPublic(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("자유게시판"));
    }

    @Test
    @DisplayName("사설 채널 생성: 올바른 요청 시 201 Created와 채널 정보를 반환해야 한다")
    void postPrivateChannel_success() throws Exception {
        // given
        List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        UserDto user1 = new UserDto(UUID.randomUUID(), "김현재","fred@naver.com", null, true);
        UserDto user2 = new UserDto(UUID.randomUUID(), "현재","fred@gmail.com", null, true);

        ChannelDto response = new ChannelDto(UUID.randomUUID(), null, null,
                List.of(user1,user2),
                Instant.now(),
                ChannelType.PRIVATE);

        given(channelService.createPrivate(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.participants.size()").value(2))
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("유저가 속한 채널 전체 조회: userId를 쿼리 파라미터로 보내면 200 OK를 반환해야 한다")
    void getAllChannelByUserId_success() throws Exception {
        // given
        UUID targetUserId = UUID.randomUUID();
        ChannelDto channel1 = new ChannelDto(UUID.randomUUID(), "채널1", "설명1", null, Instant.now(), ChannelType.PUBLIC);
        ChannelDto channel2 = new ChannelDto(UUID.randomUUID(), "채널2", "설명2", null, Instant.now(), ChannelType.PUBLIC);

        given(channelService.findAllChannelsByUserId(targetUserId)).willReturn(List.of(channel1, channel2));

        // when & then
        mockMvc.perform(get("/api/channels")
                        .param("userId", targetUserId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("채널1"));
    }

    @Test
    @DisplayName("채널 업데이트: 채널 ID와 수정 정보를 보내면 200 OK를 반환해야 한다")
    void updateChannel_success() throws Exception {
        // given
        UUID targetChannelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수정된 이름", "수정된 설명");
        ChannelDto response = new ChannelDto(targetChannelId, "수정된 이름", "수정된 설명", null, Instant.now(), ChannelType.PUBLIC);

        given(channelService.update(eq(targetChannelId), any())).willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", targetChannelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정된 이름"));
    }

    @Test
    @DisplayName("채널 삭제: 채널 ID를 보내면 성공적으로 삭제하고 204 No Content를 반환해야 한다")
    void deleteChannel_success() throws Exception {
        // given
        UUID targetChannelId = UUID.randomUUID();


        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", targetChannelId))
                .andExpect(status().isNoContent());
    }
}