package com.sprint.mission.discodeit.channel.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.channel.application.service.ChannelService;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.support.mapper.GlobalApiMapperTestConfig;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalApiMapperTestConfig.class)
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ChannelService channelService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("createPublic - 정상 요청 시 201 Created를 반환하고 올바른 값이 매핑된다.")
  void create_public_success() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("General", "Description");
    Channel channel = Channel.builder().name("General").description("Description")
        .type(ChannelType.PUBLIC).build();

    given(channelService.createPublic(any(PublicChannelCreateRequest.class))).willReturn(channel);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("General"))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andDo(print());

    ArgumentCaptor<PublicChannelCreateRequest> captor = ArgumentCaptor.forClass(
        PublicChannelCreateRequest.class);
    verify(channelService).createPublic(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("General");
    assertThat(captor.getValue().getDescription()).isEqualTo("Description");
  }

  @Test
  @DisplayName("createPublic - 이름이 비어있으면 400 Bad Request를 반환한다.")
  void create_public_fail_validation() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "Description");

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("createPrivate - 정상 요청 시 201 Created를 반환하고 올바른 값이 매핑된다.")
  void create_private_success() throws Exception {
    UUID participantId1 = UUID.randomUUID();
    UUID participantId2 = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of(participantId1, participantId2));
    Channel channel = Channel.builder().type(ChannelType.PRIVATE).build();
    ChannelDetailDto dto = new ChannelDetailDto(channel, Instant.now(), List.of());

    given(channelService.createPrivate(any(PrivateChannelCreateRequest.class))).willReturn(dto);

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"))
        .andDo(print());

    ArgumentCaptor<PrivateChannelCreateRequest> captor = ArgumentCaptor.forClass(
        PrivateChannelCreateRequest.class);
    verify(channelService).createPrivate(captor.capture());
    assertThat(captor.getValue().getParticipantIds()).containsExactly(participantId1,
        participantId2);
  }

  @Test
  @DisplayName("createPrivate - 참가자 리스트가 비어있으면 400 Bad Request를 반환한다.")
  void create_private_fail_validation() throws Exception {
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of());

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("findByUserId - 정상 조회 시 200 OK를 반환한다.")
  void findByUserId_success() throws Exception {
    UUID userId = UUID.randomUUID();
    Channel channel = Channel.builder().name("General").description("Description")
        .type(ChannelType.PUBLIC).build();
    ChannelDetailDto dto = new ChannelDetailDto(channel, Instant.now(), List.of());

    given(channelService.findAllByUserId(userId)).willReturn(List.of(dto));

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("General"))
        .andDo(print());
  }

  @Test
  @DisplayName("update - 정상 요청 시 200 OK를 반환하고 올바른 값이 매핑된다.")
  void update_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("Updated",
        "Updated Description");
    Channel channel = Channel.builder().name("Updated").description("Updated Description")
        .type(ChannelType.PUBLIC).build();
    ChannelDetailDto dto = new ChannelDetailDto(channel, Instant.now(), List.of());

    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(
        dto);

    mockMvc.perform(patch("/api/channels/{id}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"))
        .andDo(print());

    ArgumentCaptor<PublicChannelUpdateRequest> captor = ArgumentCaptor.forClass(
        PublicChannelUpdateRequest.class);
    verify(channelService).update(eq(channelId), captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("Updated");
    assertThat(captor.getValue().getDescription()).isEqualTo("Updated Description");
  }

  @Test
  @DisplayName("update - 부분 수정(이름만 있는 경우) 시 200 OK를 반환한다.")
  void update_partial_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("UpdatedName", null);
    Channel channel = Channel.builder().name("UpdatedName").description("Original Description")
        .type(ChannelType.PUBLIC).build();
    ChannelDetailDto dto = new ChannelDetailDto(channel, Instant.now(), List.of());

    given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class))).willReturn(
        dto);

    mockMvc.perform(patch("/api/channels/{id}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("UpdatedName"))
        .andDo(print());

    ArgumentCaptor<PublicChannelUpdateRequest> captor = ArgumentCaptor.forClass(
        PublicChannelUpdateRequest.class);
    verify(channelService).update(eq(channelId), captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("UpdatedName");
    assertThat(captor.getValue().getDescription()).isNull();
  }

  @Test
  @DisplayName("delete - 채널 삭제 시 204 NO_CONTENT를 반환한다.")
  void delete_success() throws Exception {
    UUID channelId = UUID.randomUUID();

    mockMvc.perform(delete("/api/channels/{id}", channelId))
        .andExpect(status().isNoContent())
        .andDo(print());

    verify(channelService).delete(channelId);
  }
}
