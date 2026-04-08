package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublicChannel_Success() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "default room");

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.name").value("general"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("공개 채널 생성 실패 - 중복 이름")
  void createPublicChannel_Fail_DuplicateName() throws Exception {
    // given
    createPublicChannel("dup-channel", "first");
    PublicChannelCreateRequest duplicate = new PublicChannelCreateRequest("dup-channel", "second");

    // when & then
    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(duplicate)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("CH002"));
  }

  @Test
  @DisplayName("채널 수정 성공")
  void updateChannel_Success() throws Exception {
    // given
    UUID channelId = createPublicChannel("before-name", "before-desc");
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest();
    request.setNewName("after-name");
    request.setNewDescription("after-desc");

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value("after-name"))
        .andExpect(jsonPath("$.description").value("after-desc"));
  }

  @Test
  @DisplayName("채널 수정 실패 - 존재하지 않는 채널")
  void updateChannel_Fail_NotFound() throws Exception {
    // given
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest();
    request.setNewName("nope");

    // when & then
    mockMvc.perform(patch("/api/channels/{channelId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CH001"));
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_Success() throws Exception {
    // given
    UUID channelId = createPublicChannel("to-delete", "desc");

    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("채널 삭제 실패 - 존재하지 않는 채널")
  void deleteChannel_Fail_NotFound() throws Exception {
    // when & then
    mockMvc.perform(delete("/api/channels/{channelId}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CH001"));
  }

  private UUID createPublicChannel(String name, String description) throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(name, description);
    MvcResult result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn();

    return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
  }
}
