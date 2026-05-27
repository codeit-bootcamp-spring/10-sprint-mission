package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ChannelController.class)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    private UUID channelId;
    private UUID userId;
    private ChannelDto channelDto;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        userId = UUID.randomUUID();

        channelDto = new ChannelDto(
                channelId,
                IsPrivate.PUBLIC,
                "공지방",
                "공지방입니다",
                List.of(),
                Instant.now()
        );
    }

    @Test
    @DisplayName("공개 채널 생성 성공")
    void createPublic_success() throws Exception {
        // given
        PublicChannelCreateRequest request =
                new PublicChannelCreateRequest("공지방", "공지방입니다");

        when(channelService.createPublic(any(PublicChannelCreateRequest.class)))
                .thenReturn(channelDto);

        // when
        ResultActions actions = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("공지방"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("공개 채널 생성 실패 - 잘못된 요청")
    void createPublic_fail_bad_request() throws Exception {
        // given
        PublicChannelCreateRequest request =
                new PublicChannelCreateRequest(null, null);

        // when
        ResultActions actions = mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void createPrivate_success() throws Exception {
        // given
        PrivateChannelCreateRequest request =
                new PrivateChannelCreateRequest(List.of(UUID.randomUUID()));

        ChannelDto privateChannelDto = new ChannelDto(
                UUID.randomUUID(),
                IsPrivate.PRIVATE,
                null,
                null,
                List.of(),
                Instant.now()
        );

        when(channelService.createPrivate(any(PrivateChannelCreateRequest.class)))
                .thenReturn(privateChannelDto);

        // when
        ResultActions actions = mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PRIVATE")) // 타입 확인
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("비공개 채널 생성 실패 - 참여자 없음")
    void createPrivate_fail_bad_request() throws Exception {
        // given
        PrivateChannelCreateRequest request =
                new PrivateChannelCreateRequest(null);

        // when
        ResultActions actions = mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널 수정 성공")
    void update_success() throws Exception {
        // given
        PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("수정채널", "수정설명");

        ChannelDto updated = new ChannelDto(
                channelId,
                IsPrivate.PUBLIC,
                "수정채널",
                "수정설명",
                List.of(),
                Instant.now()
        );

        when(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
                .thenReturn(updated);

        // when
        ResultActions actions = mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정채널"))
                .andExpect(jsonPath("$.description").value("수정설명"));
    }

    @Test
    @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
    void update_fail_not_found() throws Exception {
        // given
        PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("수정채널", "수정설명");

        when(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
                .thenThrow(new ChannelNotFoundException(channelId));

        // when
        ResultActions actions = mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void delete_success() throws Exception {
        // given
        doNothing().when(channelService).delete(channelId);

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/channels/{channelId}", channelId));

        // then
        actions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 channelId")
    void delete_fail_not_found() throws Exception {
        // given
        doThrow(new ChannelNotFoundException(channelId))
                .when(channelService).delete(channelId);

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/channels/{channelId}", channelId));

        // then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    @Test
    @DisplayName("채널 목록 조회 성공")
    void findAll_success() throws Exception {
        // given
        List<ChannelDto> list = List.of(channelDto);

        when(channelService.findAllByUserId(userId)).thenReturn(list);

        // when
        ResultActions actions = mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("공지방"));
    }

    @Test
    @DisplayName("채널 목록 조회 - 없음")
    void findAll_empty() throws Exception {
        // given
        when(channelService.findAllByUserId(userId)).thenReturn(List.of());

        // when
        ResultActions actions = mockMvc.perform(get("/api/channels")
                .param("userId", userId.toString()));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
