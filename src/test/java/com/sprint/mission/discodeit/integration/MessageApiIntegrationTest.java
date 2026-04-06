package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("메시지 생성 성공")
  void createMessage_Success() throws Exception {
    // given
    UUID authorId = createUser("msg-user", "msg-user@test.com");
    UUID channelId = createPublicChannel("msg-channel");
    MessageCreateRequest request = new MessageCreateRequest(channelId, authorId, "hello world");

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(jsonPart("messageCreateRequest", request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.content").value("hello world"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.id").value(authorId.toString()));
  }

  @Test
  @DisplayName("메시지 생성 실패 - 존재하지 않는 채널")
  void createMessage_Fail_ChannelNotFound() throws Exception {
    // given
    UUID authorId = createUser("msg-user2", "msg-user2@test.com");
    MessageCreateRequest request = new MessageCreateRequest(UUID.randomUUID(), authorId, "fail message");

    // when & then
    mockMvc.perform(multipart("/api/messages")
            .file(jsonPart("messageCreateRequest", request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("CH001"));
  }

  @Test
  @DisplayName("메시지 수정 성공")
  void updateMessage_Success() throws Exception {
    // given
    UUID authorId = createUser("update-user", "update-user@test.com");
    UUID channelId = createPublicChannel("update-channel");
    UUID messageId = createMessage(channelId, authorId, "before");

    MessageUpdateRequest request = new MessageUpdateRequest();
    request.setNewContent("after");

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("after"));
  }

  @Test
  @DisplayName("메시지 수정 실패 - 존재하지 않는 메시지")
  void updateMessage_Fail_NotFound() throws Exception {
    // given
    MessageUpdateRequest request = new MessageUpdateRequest();
    request.setNewContent("after");

    // when & then
    mockMvc.perform(patch("/api/messages/{messageId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("M001"));
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_Success() throws Exception {
    // given
    UUID authorId = createUser("delete-user", "delete-user@test.com");
    UUID channelId = createPublicChannel("delete-channel");
    UUID messageId = createMessage(channelId, authorId, "to delete");

    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지")
  void deleteMessage_Fail_NotFound() throws Exception {
    // when & then
    mockMvc.perform(delete("/api/messages/{messageId}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("M001"));
  }

  @Test
  @DisplayName("메시지 목록 조회 성공")
  void getMessages_Success() throws Exception {
    // given
    UUID authorId = createUser("list-user", "list-user@test.com");
    UUID channelId = createPublicChannel("list-channel");
    createMessage(channelId, authorId, "first");
    createMessage(channelId, authorId, "second");

    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.hasNext").value(false));
  }

  @Test
  @DisplayName("메시지 목록 조회 실패 - channelId 누락")
  void getMessages_Fail_MissingChannelId() throws Exception {
    // when & then
    mockMvc.perform(get("/api/messages")
            .param("size", "10"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value("C002"));
  }

  private UUID createUser(String username, String email) throws Exception {
    UserCreateRequest request = new UserCreateRequest(username, email, "password123");
    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(jsonPart("userCreateRequest", request)))
        .andExpect(status().isCreated())
        .andReturn();

    return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
  }

  private UUID createPublicChannel(String name) throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest(name, "desc");
    MvcResult result = mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn();

    return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
  }

  private UUID createMessage(UUID channelId, UUID authorId, String content) throws Exception {
    MessageCreateRequest request = new MessageCreateRequest(channelId, authorId, content);
    MvcResult result = mockMvc.perform(multipart("/api/messages")
            .file(jsonPart("messageCreateRequest", request)))
        .andExpect(status().isCreated())
        .andReturn();

    return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
  }

  private MockMultipartFile jsonPart(String partName, Object body) throws Exception {
    return new MockMultipartFile(
        partName,
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8)
    );
  }
}
