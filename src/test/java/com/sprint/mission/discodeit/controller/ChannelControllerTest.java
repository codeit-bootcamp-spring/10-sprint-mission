package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc; // 가짜 HTTP 요청 객체

  @Autowired
  private ObjectMapper objectMapper; // 객체를 JSON 문자열로 변환하기 위한 객체

  @MockitoBean
  private ChannelService channelService;

  @MockitoBean
  private ChannelMapper channelMapper;

  @MockitoBean
  private UserMapper userMapper;

  @Test
  @DisplayName("성공: 공개 채널 생성 시 201 Created와 채널 정보를 반환한다")
  void createPublicChannel_Success() throws Exception {
    // given
    PublicChannelCreateRequest requestDto = new PublicChannelCreateRequest("General", "자유 게시판");
    Channel mockChannel = new Channel("General", "자유 게시판", ChannelType.PUBLIC);
    ChannelDto mockResponseDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "General",
        "자유 게시판", List.of(), null);

    given(channelService.createPublicChannel(any(), any())).willReturn(mockChannel);
    given(channelMapper.toDto(eq(mockChannel), any(), any())).willReturn(mockResponseDto);

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("General"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("실패: 공개 채널 생성 시 이름이 없거나 30자를 초과하면 400 Bad Request를 반환한다")
  void createPublicChannel_Fail_BadRequest() throws Exception {
    // given (이름을 빈 문자열로 설정하여 @NotBlank 위반)
    PublicChannelCreateRequest badRequest = new PublicChannelCreateRequest("", "설명");

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(badRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("성공: 비공개 채널 생성 시 201 Created와 채널 정보를 반환한다")
  void createPrivateChannel_Success() throws Exception {
    // given
    PrivateChannelCreateRequest requestDto = new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID(), UUID.randomUUID()));
    Channel mockChannel = new Channel(null, null, ChannelType.PRIVATE);
    ChannelDto mockResponseDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null,
        List.of(), null);

    given(channelService.createPrivateChannel(any())).willReturn(mockChannel);
    given(channelMapper.toDto(eq(mockChannel), any(), any())).willReturn(mockResponseDto);

    // when & then
    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  @DisplayName("성공: 특정 유저가 속한 채널 목록 조회 시 200 OK를 반환한다")
  void findAllByUserId_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    Channel mockChannel = new Channel("General", "설명", ChannelType.PUBLIC);
    ChannelDto mockDto = new ChannelDto(mockChannel.getId(), ChannelType.PUBLIC, "General", "설명",
        List.of(), Instant.now());

    // 서비스 메서드 모킹
    given(channelService.findAllByUserId(userId)).willReturn(List.of(mockChannel));
    given(channelService.getLastMessagesAtMap(any())).willReturn(
        Map.of(mockChannel.getId(), Instant.now()));
    given(channelService.getParticipantsMap(any())).willReturn(
        Map.of(mockChannel.getId(), List.of()));
    given(channelMapper.toDto(eq(mockChannel), any(), any())).willReturn(mockDto);

    // when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("General"));
  }

  @Test
  @DisplayName("성공: 채널 정보를 수정하면 200 OK를 반환한다")
  void update_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest requestDto = new PublicChannelUpdateRequest("Updated Name",
        "Updated Desc");
    Channel mockChannel = new Channel("Updated Name", "Updated Desc", ChannelType.PUBLIC);
    ChannelDto mockDto = new ChannelDto(channelId, ChannelType.PUBLIC, "Updated Name",
        "Updated Desc", List.of(), null);

    given(channelService.update(eq(channelId), any(), any())).willReturn(mockChannel);
    given(channelMapper.toDto(eq(mockChannel), any(), any())).willReturn(mockDto);

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Name"));
  }

  @Test
  @DisplayName("성공: 채널을 삭제하면 204 No Content를 반환한다")
  void delete_Success() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }
}