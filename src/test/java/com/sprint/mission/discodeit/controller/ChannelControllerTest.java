package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPrivate;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequestPublic;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.response.ChannelDTO;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateNotAllowed;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@ActiveProfiles("test")
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    private UUID channelId;
    private ChannelDTO channelDTO;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        channelDTO = new ChannelDTO(channelId, ChannelType.PUBLIC, "테스트", "테스트 채널입니다.", List.of(), Instant.now());
    }

    @Test
    @DisplayName("공개 채널 생성 성공 - 201 Created + ChannelDTO 반환")
    void postPublicChannel_성공() throws Exception {
        // given
        ChannelCreateRequestPublic request = new ChannelCreateRequestPublic("테스트", "테스트 채널입니다.");
        given(channelService.createPublic(any())).willReturn(channelDTO);

        // when, then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("테스트"));
    }

    @Test
    @DisplayName("공개 채널 생성 실패 - name이 빈 값이면 400 Bad Request")
    void postPublicChannel_실패_이름없음() throws Exception {
        // given: @NotBlank 위반
        ChannelCreateRequestPublic request = new ChannelCreateRequestPublic("", "설명");

        // when, then: 서비스 호출 없이 validation 단계에서 400 반환
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").value("채널 이름은 필수입니다."));

        then(channelService).should(never()).createPublic(any());
    }


    @Test
    @DisplayName("비공개 채널 생성 성공 - 201 Created + ChannelDTO 반환")
    void postPrivateChannel_성공() throws Exception {
        // given
        ChannelDTO privateChannelDTO = new ChannelDTO(channelId, ChannelType.PRIVATE, null, null, List.of(), Instant.now());
        ChannelCreateRequestPrivate request = new ChannelCreateRequestPrivate(List.of(UUID.randomUUID()));
        given(channelService.createPrivate(any())).willReturn(privateChannelDTO);

        // when, then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("비공개 채널 생성 실패 - participantIds가 비어있으면 400 Bad Request")
    void postPrivateChannel_실패_참여자없음() throws Exception {
        // given: @NotEmpty 위반
        ChannelCreateRequestPrivate request = new ChannelCreateRequestPrivate(List.of());

        // when, then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.participantIds").value("참여자는 최소 1명 이상입니다."));

        then(channelService).should(never()).createPrivate(any());
    }

    // ===================== getChannel =====================

    @Test
    @DisplayName("채널 단건 조회 성공 - 200 OK + ChannelDTO 반환")
    void getChannel_성공() throws Exception {
        // given
        given(channelService.find(channelId)).willReturn(channelDTO);

        // when, then
        mockMvc.perform(get("/api/channels/{channelId}", channelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("테스트"));
    }

    @Test
    @DisplayName("채널 단건 조회 실패 - 존재하지 않는 channelId면 404 Not Found")
    void getChannel_실패_없는채널() throws Exception {
        // given
        given(channelService.find(channelId)).willThrow(new ChannelNotFoundException(channelId));

        // when, then
        mockMvc.perform(get("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound());
    }

    // ===================== getAllChannels =====================

    @Test
    @DisplayName("유저 채널 목록 조회 성공 - 200 OK + 채널 리스트 반환")
    void getAllChannels_성공() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(channelService.findAllByUserID(userId)).willReturn(List.of(channelDTO));

        // when, then
        mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("테스트"));
    }

    @Test
    @DisplayName("유저 채널 목록 조회 성공 - 참여 채널 없을 경우 빈 리스트 반환")
    void getAllChannels_빈리스트() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        given(channelService.findAllByUserID(userId)).willReturn(List.of());

        // when, then
        mockMvc.perform(get("/api/channels").param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ===================== updateChannel =====================

    @Test
    @DisplayName("채널 수정 성공 - 200 OK + 수정된 ChannelDTO 반환")
    void updateChannel_성공() throws Exception {
        // given
        ChannelUpdateRequest request = new ChannelUpdateRequest("새 이름", "새 설명");
        ChannelDTO updatedDTO = new ChannelDTO(channelId, ChannelType.PUBLIC, "새 이름", "새 설명", List.of(), Instant.now());
        given(channelService.update(eq(channelId), any())).willReturn(updatedDTO);

        // when, then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("새 이름"))
                .andExpect(jsonPath("$.description").value("새 설명"));
    }

    @Test
    @DisplayName("채널 수정 실패 - 존재하지 않는 채널이면 404 Not Found")
    void updateChannel_실패_없는채널() throws Exception {
        // given
        ChannelUpdateRequest request = new ChannelUpdateRequest("새 이름", "새 설명");
        given(channelService.update(eq(channelId), any())).willThrow(new ChannelNotFoundException(channelId));

        // when, then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 수정 실패 - Private 채널은 수정 불가 400 Bad Request")
    void updateChannel_실패_Private채널() throws Exception {
        // given
        ChannelUpdateRequest request = new ChannelUpdateRequest("새 이름", "새 설명");
        given(channelService.update(eq(channelId), any())).willThrow(new PrivateChannelUpdateNotAllowed(channelId));

        // when, then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널 수정 실패 - newName이 빈 값이면 400 Bad Request")
    void updateChannel_실패_이름없음() throws Exception {
        // given: @NotBlank 위반
        ChannelUpdateRequest request = new ChannelUpdateRequest("", "새 설명");

        // when, then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.newName").value("채널 이름은 필수입니다."));

        then(channelService).should(never()).update(any(), any());
    }

    // ===================== deleteChannel =====================

    @Test
    @DisplayName("채널 삭제 성공 - 204 No Content")
    void deleteChannel_성공() throws Exception {
        // given
        willDoNothing().given(channelService).deleteChannel(channelId);

        // when, then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());

        then(channelService).should().deleteChannel(channelId);
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널이면 404 Not Found")
    void deleteChannel_실패_없는채널() throws Exception {
        // given
        willThrow(new ChannelNotFoundException(channelId)).given(channelService).deleteChannel(channelId);

        // when, then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound());
    }
}
