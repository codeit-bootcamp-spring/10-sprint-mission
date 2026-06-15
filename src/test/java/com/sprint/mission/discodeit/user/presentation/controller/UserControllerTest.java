package com.sprint.mission.discodeit.user.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.user.application.service.UserService;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("find - 정상적으로 유저를 조회하면 200 OK와 UserResponse를 반환한다.")
  void find_success() throws Exception {
    UUID id = UUID.randomUUID();
    UserResponse response = UserResponse.builder().id(id).username("user1").email("test@test.com")
        .build();

    given(userService.find(id)).willReturn(response);

    mockMvc.perform(get("/users/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.username").value("user1"))
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andDo(print());
  }

  @Test
  @DisplayName("create - 유효한 요청시 Service에 올바른 DTO가 전달되며 201 CREATED를 반환한다.")
  void create_success() throws Exception {
    UserCreateRequest request = new UserCreateRequest("user1", "test@test.com", "password");
    UserResponse response = UserResponse.builder().id(UUID.randomUUID()).username("user1")
        .email("test@test.com").build();

    given(userService.create(any(UserCreateRequest.class))).willReturn(response);

    mockMvc.perform(post("/users")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("user1"))
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andDo(print());

    ArgumentCaptor<UserCreateRequest> captor = ArgumentCaptor.forClass(UserCreateRequest.class);
    verify(userService).create(captor.capture());
    UserCreateRequest capturedRequest = captor.getValue();
    assertThat(capturedRequest.getUsername()).isEqualTo("user1");
    assertThat(capturedRequest.getEmail()).isEqualTo("test@test.com");
    assertThat(capturedRequest.getPassword()).isEqualTo("password");
  }

  @ParameterizedTest
  @MethodSource("provideInvalidCreateRequests")
  @DisplayName("create - @Valid 검증 실패 시 400 Bad Request를 반환한다.")
  void create_fail_validation(UserCreateRequest request) throws Exception {
    mockMvc.perform(post("/users")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("update - 유효한 요청시 Service에 올바른 DTO가 전달되며 200 OK를 반환한다.")
  void update_success() throws Exception {
    UUID id = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("updatedUser", "updated@test.com", "newpass",
        null);
    UserResponse response = UserResponse.builder().id(id).username("updatedUser")
        .email("updated@test.com").build();

    given(userService.update(eq(id), any(UserUpdateRequest.class))).willReturn(response);

    mockMvc.perform(patch("/users/{id}", id)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updatedUser"))
        .andExpect(jsonPath("$.email").value("updated@test.com"))
        .andDo(print());

    ArgumentCaptor<UserUpdateRequest> captor = ArgumentCaptor.forClass(UserUpdateRequest.class);
    verify(userService).update(eq(id), captor.capture());
    UserUpdateRequest capturedRequest = captor.getValue();
    assertThat(capturedRequest.getUsername()).isEqualTo("updatedUser");
    assertThat(capturedRequest.getEmail()).isEqualTo("updated@test.com");
    assertThat(capturedRequest.getPassword()).isEqualTo("newpass");
    assertThat(capturedRequest.getProfileId()).isNull();
  }

  @ParameterizedTest
  @MethodSource("provideInvalidUpdateRequests")
  @DisplayName("update - @Valid 검증 실패 시 400 Bad Request를 반환한다.")
  void update_fail_validation(UserUpdateRequest request) throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc.perform(patch("/users/{id}", id)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("find - 존재하지 않는 유저 조회 시 404 Not Found를 반환한다.")
  void find_fail_notFound() throws Exception {
    UUID id = UUID.randomUUID();

    given(userService.find(id)).willThrow(new UserException(UserErrorCode.USERID_NOT_FOUND, id));

    mockMvc.perform(get("/users/{id}", id))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @DisplayName("findAll - 모든 유저를 조회하면 200 OK와 리스트를 반환한다.")
  void findAll_success() throws Exception {
    UserResponse response1 = UserResponse.builder().id(UUID.randomUUID()).username("user1").email("user1@test.com").build();
    UserResponse response2 = UserResponse.builder().id(UUID.randomUUID()).username("user2").email("user2@test.com").build();
    
    given(userService.findAll()).willReturn(java.util.List.of(response1, response2));

    mockMvc.perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].username").value("user1"))
        .andExpect(jsonPath("$[1].username").value("user2"))
        .andDo(print());
  }

  @Test
  @DisplayName("delete - 유저 삭제 시 204 NO_CONTENT를 반환한다.")
  void delete_success() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(delete("/users/{id}", id))
        .andExpect(status().isNoContent())
        .andDo(print());

    verify(userService).delete(id);
  }

  @Test
  @DisplayName("delete - 존재하지 않는 유저 삭제 시 404 Not Found를 반환한다.")
  void delete_fail_notFound() throws Exception {
    UUID id = UUID.randomUUID();

    org.mockito.Mockito.doThrow(new UserException(UserErrorCode.USERID_NOT_FOUND, id))
        .when(userService).delete(id);

    mockMvc.perform(delete("/users/{id}", id))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @DisplayName("create - 중복된 username으로 생성 시 400 Bad Request를 반환한다.")
  void create_fail_duplicateUsername() throws Exception {
    UserCreateRequest request = new UserCreateRequest("user1", "test@test.com", "password");

    given(userService.create(any(UserCreateRequest.class)))
        .willThrow(
            new UserException(UserErrorCode.USERNAME_ALREADY_EXIST, Map.of("username", "user1")));

    mockMvc.perform(post("/users")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("update - 존재하지 않는 유저 수정 시 404 Not Found를 반환한다.")
  void update_fail_notFound() throws Exception {
    UUID id = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("updatedUser", "updated@test.com", "newpass", null);

    given(userService.update(eq(id), any(UserUpdateRequest.class)))
        .willThrow(new com.sprint.mission.discodeit.user.domain.exception.UserException(com.sprint.mission.discodeit.user.domain.exception.UserErrorCode.USERID_NOT_FOUND, id));

    mockMvc.perform(patch("/users/{id}", id)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  @DisplayName("updateRole - 유효한 요청시 200 OK와 수정된 UserResponse를 반환한다.")
  void updateRole_success() throws Exception {
    UUID id = UUID.randomUUID();
    com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest request = new com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest(id, com.sprint.mission.discodeit.user.domain.entity.constant.UserRole.ADMIN);
    UserResponse response = UserResponse.builder().id(id).username("user1").email("test@test.com").build();

    given(userService.updateRole(any(com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest.class))).willReturn(response);

    mockMvc.perform(put("/users/role")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("user1"))
        .andDo(print());
        
    ArgumentCaptor<com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest> captor = ArgumentCaptor.forClass(com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest.class);
    verify(userService).updateRole(captor.capture());
    assertThat(captor.getValue().getId()).isEqualTo(id);
    assertThat(captor.getValue().getRole()).isEqualTo(com.sprint.mission.discodeit.user.domain.entity.constant.UserRole.ADMIN);
  }

  @Test
  @DisplayName("updateRole - 존재하지 않는 유저 역할 수정 시 404 Not Found를 반환한다.")
  void updateRole_fail_notFound() throws Exception {
    UUID id = UUID.randomUUID();
    com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest request = new com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest(id, com.sprint.mission.discodeit.user.domain.entity.constant.UserRole.ADMIN);

    given(userService.updateRole(any(com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest.class)))
        .willThrow(new com.sprint.mission.discodeit.user.domain.exception.UserException(com.sprint.mission.discodeit.user.domain.exception.UserErrorCode.USERID_NOT_FOUND, id));

    mockMvc.perform(put("/users/role")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  private static Stream<UserCreateRequest> provideInvalidCreateRequests() {
    return Stream.of(
        new UserCreateRequest("", "test@test.com", "pass"),
        new UserCreateRequest("user1", "invalid-email", "pass"),
        new UserCreateRequest("user1", "test@test.com", "")
    );
  }

  private static Stream<UserUpdateRequest> provideInvalidUpdateRequests() {
    return Stream.of(
        new UserUpdateRequest(null, "invalid-email", null, null)
    );
  }
}
