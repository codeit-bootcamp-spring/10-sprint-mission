package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

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
  private UserService userService;

  private RequestPostProcessor asUser(UUID userId, UserRole role) {
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, false, role);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");
    Authentication auth = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    return authentication(auth);
  }

  @Test
  @DisplayName("사용자 생성 API 통합 테스트")
  void createUser_Success() throws Exception {

    UserCreateRequest createRequest = new UserCreateRequest(
        "testuser",
        "test@example.com",
        "Password1!"
    );

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(createRequest)
    );

    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "profile.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "test-image".getBytes()
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequestPart)
            .file(profilePart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.username", is("testuser")))
        .andExpect(jsonPath("$.email", is("test@example.com")))
        .andExpect(jsonPath("$.profile.fileName", is("profile.jpg")))

        .andExpect(jsonPath("$.online", is(false)));
  }

  @Test
  @DisplayName("사용자 생성 실패 API 통합 테스트 - 유효하지 않은 요청")
  void createUser_Failure_InvalidRequest() throws Exception {

    UserCreateRequest invalidRequest = new UserCreateRequest(
        "t",
        "invalid-email",
        "short"
    );

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalidRequest)
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequestPart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("모든 사용자 조회 API 통합 테스트")
  void findAllUsers_Success() throws Exception {

    UserCreateRequest userRequest1 = new UserCreateRequest(
        "user1",
        "user1@example.com",
        "Password1!"
    );

    UserCreateRequest userRequest2 = new UserCreateRequest(
        "user2",
        "user2@example.com",
        "Password1!"
    );

    userService.create(userRequest1, Optional.empty());
    userService.create(userRequest2, Optional.empty());

    mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .with(asUser(UUID.randomUUID(), UserRole.USER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].username", hasItems("user1", "user2")))
        .andExpect(jsonPath("$[*].email", hasItems("user1@example.com", "user2@example.com")));
  }

  @Test
  @DisplayName("사용자 업데이트 API 통합 테스트")
  void updateUser_Success() throws Exception {

    UserCreateRequest createRequest = new UserCreateRequest(
        "originaluser",
        "original@example.com",
        "Password1!"
    );

    UserDto createdUser = userService.create(createRequest, Optional.empty());
    UUID userId = createdUser.id();

    UserUpdateRequest updateRequest = new UserUpdateRequest(
        "updateduser",
        "updated@example.com",
        "UpdatedPassword1!"
    );

    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(updateRequest)
    );

    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "updated-profile.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "updated-image".getBytes()
    );

    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userUpdateRequestPart)
            .file(profilePart)
            .with(csrf())
            .with(asUser(userId, UserRole.USER))
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(userId.toString())))
        .andExpect(jsonPath("$.username", is("updateduser")))
        .andExpect(jsonPath("$.email", is("updated@example.com")))
        .andExpect(jsonPath("$.profile.fileName", is("updated-profile.jpg")));
  }

  @Test
  @DisplayName("사용자 업데이트 실패 API 통합 테스트 - 존재하지 않는 사용자")
  void updateUser_Failure_UserNotFound() throws Exception {

    UUID nonExistentUserId = UUID.randomUUID();
    UserUpdateRequest updateRequest = new UserUpdateRequest(
        "updateduser",
        "updated@example.com",
        "UpdatedPassword1!"
    );

    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(updateRequest)
    );

    mockMvc.perform(multipart("/api/users/{userId}", nonExistentUserId)
            .file(userUpdateRequestPart)
            .with(csrf())
            .with(asUser(nonExistentUserId, UserRole.USER))
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            }))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("사용자 삭제 API 통합 테스트")
  void deleteUser_Success() throws Exception {

    UserCreateRequest createRequest = new UserCreateRequest(
        "deleteuser",
        "delete@example.com",
        "Password1!"
    );

    UserDto createdUser = userService.create(createRequest, Optional.empty());
    UUID userId = createdUser.id();

    mockMvc.perform(delete("/api/users/{userId}", userId)
            .with(csrf())
            .with(asUser(userId, UserRole.USER)))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/users")
            .with(asUser(UUID.randomUUID(), UserRole.USER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id == '" + userId + "')]").doesNotExist());
  }

  @Test
  @DisplayName("사용자 삭제 실패 API 통합 테스트 - 존재하지 않는 사용자")
  void deleteUser_Failure_UserNotFound() throws Exception {

    UUID nonExistentUserId = UUID.randomUUID();

    mockMvc.perform(delete("/api/users/{userId}", nonExistentUserId)
            .with(csrf())
            .with(asUser(nonExistentUserId, UserRole.USER)))
        .andExpect(status().isNotFound());
  }
}
