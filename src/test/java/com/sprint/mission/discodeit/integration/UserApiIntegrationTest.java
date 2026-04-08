package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
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
class UserApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("사용자 생성 성공")
  void createUser_Success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("alice", "alice@test.com", "password123");

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(jsonPart("userCreateRequest", request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.username").value("alice"))
        .andExpect(jsonPath("$.email").value("alice@test.com"));
  }

  @Test
  @DisplayName("사용자 생성 실패 - 중복 이메일")
  void createUser_Fail_DuplicateEmail() throws Exception {
    // given
    createUser("user1", "dup@test.com", "password123");
    UserCreateRequest duplicate = new UserCreateRequest("user2", "dup@test.com", "password123");

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(jsonPart("userCreateRequest", duplicate)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("U002"));
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void updateUser_Success() throws Exception {
    // given
    UUID userId = createUser("before", "before@test.com", "password123");
    UserUpdateRequest request = new UserUpdateRequest("after", "after@test.com", "newpassword123");

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(jsonPart("userUpdateRequest", request))
            .with(r -> {
              r.setMethod("PATCH");
              return r;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("after"))
        .andExpect(jsonPath("$.email").value("after@test.com"));
  }

  @Test
  @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자")
  void updateUser_Fail_NotFound() throws Exception {
    // given
    UserUpdateRequest request = new UserUpdateRequest("after", "after@test.com", "newpassword123");

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", UUID.randomUUID())
            .file(jsonPart("userUpdateRequest", request))
            .with(r -> {
              r.setMethod("PATCH");
              return r;
            }))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("U001"));
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void deleteUser_Success() throws Exception {
    // given
    UUID userId = createUser("delete-me", "delete@test.com", "password123");

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent());

    assertThat(userRepository.findById(userId)).isEmpty();
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
  void deleteUser_Fail_NotFound() throws Exception {
    // when & then
    mockMvc.perform(delete("/api/users/{userId}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("U001"));
  }

  @Test
  @DisplayName("사용자 목록 조회 성공")
  void getUsers_Success() throws Exception {
    // given
    createUser("list-1", "list1@test.com", "password123");
    createUser("list-2", "list2@test.com", "password123");

    // when & then
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.username == 'list-1')]").isNotEmpty())
        .andExpect(jsonPath("$[?(@.username == 'list-2')]").isNotEmpty());
  }

  @Test
  @DisplayName("사용자 목록 조회 실패 - 지원하지 않는 Accept")
  void getUsers_Fail_NotAcceptable() throws Exception {
    // when & then
    mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_XML))
        .andExpect(status().isNotAcceptable());
  }

  private UUID createUser(String username, String email, String password) throws Exception {
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    MvcResult result = mockMvc.perform(multipart("/api/users")
            .file(jsonPart("userCreateRequest", request)))
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
