package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc; // 가짜 HTTP 요청 객체

  @Autowired
  private ObjectMapper objectMapper; // 객체를 JSON 문자열로 변환하기 위한 객체

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @MockitoBean
  private UserMapper userMapper;

  @MockitoBean
  private UserStatusMapper userStatusMapper;

  @Test
  @DisplayName("성공: 프로필 사진을 포함한 회원가입 요청 시 201 응답과 DTO를 반환한다")
  void create_Success() throws Exception {
    // given
    // 1. JSON 형태의 RequestPart 가짜 데이터 생성
    UserCreateRequest requestDto = new UserCreateRequest("tester", "test@test.com", "password123");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "userCreateRequest",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8)
    );

    // 2. 프로필 사진 가짜 파일 생성
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "dummy_image_data".getBytes()
    );

    // 3. Service와 Mapper 가짜 응답 세팅
    User mockUser = new User("tester", "test@test.com", "password123", null);
    UserDto mockResponseDto = new UserDto(UUID.randomUUID(), "tester", "test@test.com", null, true);

    given(userService.create(any(), any(), any(), any())).willReturn(mockUser);
    given(userMapper.toDto(mockUser)).willReturn(mockResponseDto);

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .file(profilePart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated()) // HTTP 201 검증
        .andExpect(jsonPath("$.username").value("tester")) // JSON 응답값 검증
        .andExpect(jsonPath("$.email").value("test@test.com"));
  }

  @Test
  @DisplayName("실패: 회원가입 시 필수 값이나 정규식을 어기면 400 Bad Request를 반환한다 (@Valid 검증)")
  void create_Fail_BadRequest() throws Exception {
    // given
    // 이메일 형식 위반, 비밀번호 정규식 위반
    UserCreateRequest badRequest = new UserCreateRequest("tester", "invalid_email", "123");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest", "userCreateRequest", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(badRequest).getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mockMvc.perform(multipart("/api/users")
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest()); // HTTP 400 에러가 터져야 성공
  }

  @Test
  @DisplayName("성공: 유저 전체 목록을 조회하면 200 OK를 반환한다")
  void findAll_Success() throws Exception {
    // given
    User mockUser = new User("tester", "test@test.com", "pw", null);
    UserDto mockDto = new UserDto(UUID.randomUUID(), "tester", "test@test.com", null, false);

    given(userService.findAll()).willReturn(List.of(mockUser));
    given(userMapper.toDto(mockUser)).willReturn(mockDto);

    // when & then
    mockMvc.perform(get("/api/users")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].username").value("tester"))
        .andExpect(jsonPath("$[0].online").value(false));
  }

  @Test
  @DisplayName("성공: 프로필 사진을 포함해 유저 정보를 수정하면 200 OK를 반환한다")
  void update_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest requestDto = new UserUpdateRequest("newName", "new@test.com", "newPass123");
    MockMultipartFile requestPart = new MockMultipartFile(
        "userUpdateRequest",
        "userUpdateRequest",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8)
    );

    User mockUser = new User("newName", "new@test.com", "newPass123", null);
    UserDto mockDto = new UserDto(userId, "newName", "new@test.com", null, true);

    given(userService.update(eq(userId), any(), any(), any(), any())).willReturn(mockUser);
    given(userMapper.toDto(mockUser)).willReturn(mockDto);

    // when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(requestPart)
            .with(request -> {
              request.setMethod("PATCH");
              return request;
            })
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("newName"));
  }

  @Test
  @DisplayName("성공: 유저 상태 정보를 업데이트하면 200 OK를 반환한다")
  void updateStatus_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    UserStatusUpdateRequest requestDto = new UserStatusUpdateRequest(now);

    User mockUser = new User("tester", "test@test.com", "password123", null);
    UserStatus mockStatus = new UserStatus(mockUser, now);
    UserStatusDto mockDto = new UserStatusDto(UUID.randomUUID(), userId, now);

    given(userStatusService.updateByUserId(eq(userId), any())).willReturn(mockStatus);
    given(userStatusMapper.toDto(mockStatus)).willReturn(mockDto);

    // when & then
    mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("성공: 유저를 삭제하면 204 No Content를 반환한다")
  void delete_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // when & then
    mockMvc.perform(delete("/api/users/{userId}", userId))
        .andExpect(status().isNoContent()); // HTTP 204 검증
  }
}