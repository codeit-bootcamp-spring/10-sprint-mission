package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ChannelRepository channelRepository;

  @Nested
  @DisplayName("POST /api/channels/public - 공개 채널 생성")
  class CreatePublicChannel {

    @Test
    @DisplayName("성공: 채널 생성 시 DB에 저장된다")
    void success() throws Exception {
      // given
      PublicChannelCreateRequest requestDto = new PublicChannelCreateRequest("공개 채널", "채널 설명");

      // when & then
      mockMvc.perform(post("/api/channels/public").contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestDto)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.name").value("공개 채널"));
      assertThat(channelRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("실패: 채널명이 공백이면 400 에러가 발생한다")
    void fail_blankName() throws Exception {
      // given
      PublicChannelCreateRequest requestDto = new PublicChannelCreateRequest("", "채널 설명");

      // when & then
      mockMvc.perform(post("/api/channels/public").contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestDto)))
          .andExpect(status().isBadRequest());
      assertThat(channelRepository.findAll()).isEmpty();
    }
  }

  @Nested
  @DisplayName("PATCH /api/channels/{channelId} - 채널 수정")
  class UpdateChannel {

    @Test
    @DisplayName("성공: 채널 정보 수정 시 DB에 반영된다")
    void success() throws Exception {
      // given
      Channel savedChannel = channelRepository.save(
          new Channel("수정 전 공개 채널", "수정 전 채널 설명", ChannelType.PUBLIC));
      PublicChannelUpdateRequest requestDto = new PublicChannelUpdateRequest("수정 후 공개 채널",
          "수정 후 채널 설명");

      // when & then
      mockMvc.perform(patch("/api/channels/{channelId}", savedChannel.getId()).contentType(
              MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
          .andExpect(status().isOk());
      Channel updatedChannel = channelRepository.findById(savedChannel.getId()).orElseThrow();
      assertThat(updatedChannel.getName()).isEqualTo("수정 후 공개 채널");
    }

    @Test
    @DisplayName("실패: 이름이 너무 길면 400 에러가 발생한다")
    void fail_nameTooLong() throws Exception {
      // given
      Channel savedChannel = channelRepository.save(
          new Channel("수정 전 공개 채널", "수정 전 채널 설명", ChannelType.PUBLIC));
      String tooLongName = "서른 자가 넘어 가는 채널 이름입니다~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~";
      PublicChannelUpdateRequest requestDto = new PublicChannelUpdateRequest(tooLongName,
          "New Desc");

      // when & then
      mockMvc.perform(patch("/api/channels/{channelId}", savedChannel.getId()).contentType(
              MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /api/channels/{channelId} - 채널 삭제")
  class DeleteChannel {

    @Test
    @DisplayName("성공: 채널 삭제 시 DB에서 지워진다")
    void success() throws Exception {
      // given
      Channel savedChannel = channelRepository.save(
          new Channel("공개 채널", "채널 설명", ChannelType.PUBLIC));

      // when & then
      mockMvc.perform(delete("/api/channels/{channelId}", savedChannel.getId()))
          .andExpect(status().isNoContent());
      assertThat(channelRepository.findById(savedChannel.getId())).isEmpty();
    }

    @Test
    @DisplayName("실패: 존재하지 않는 채널 삭제 시 예외가 발생한다")
    void fail_notFound() throws Exception {
      // given: 존재하지 않는 채널 ID

      // when & then
      mockMvc.perform(delete("/api/channels/{channelId}", UUID.randomUUID()))
          .andExpect(status().is4xxClientError());
    }
  }
}
