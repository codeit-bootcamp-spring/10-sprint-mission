package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;
    @MockitoBean private UserStatusService userStatusService;

    @Test
    @DisplayName("user 등록 성공 시 201 Created 반환")
    void create_user_success() throws Exception {
        CreateUserRequestDto request = new CreateUserRequestDto("tester", "test@test.com", "password123", null);
        UserDto response = new UserDto(UUID.randomUUID(), "tester", "test@test.com", null, true);

        given(userService.create(any())).willReturn(response);

        // UserController의 @RequestPart("userCreateRequest")와 일치하도록 세팅
        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "request.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("tester"));
    }

    @Test
    @DisplayName("전체 user 목록 조회 성공 시 200 OK 반환")
    void find_all_users_success() throws Exception {
        UserDto userDto = new UserDto(UUID.randomUUID(), "tester", "test@test.com", null, true);
        given(userService.findAll()).willReturn(List.of(userDto));

        mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("tester"));
    }

    @Test
    @DisplayName("user 정보 수정 성공 시 200 OK 반환")
    void update_user_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UpdateUserRequestDto request = new UpdateUserRequestDto("newTester", "new@test.com", "newPassword", null);
        UserDto response = new UserDto(userId, "newTester", "new@test.com", null, true);

        given(userService.update(eq(userId), any())).willReturn(response);

        // when & then
        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest",
            "request.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(requestPart)
                .with(req -> { req.setMethod("PATCH"); return req; }) // PATCH로 강제 변환
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("newTester"));
    }

    @Test
    @DisplayName("user 삭제 성공 시 204 No Content 반환")
    void delete_user_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("user 상태 업데이트 성공 시 200 OK 반환")
    void update_user_status_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UpdateUserStatusRequestDto request = new UpdateUserStatusRequestDto(Instant.now());
        UserStatusDto response = new UserStatusDto(UUID.randomUUID(), userId, Instant.now());

        given(userStatusService.updateByUserId(eq(userId), any())).willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    @DisplayName("user 등록 실패 시 400 Bad Request 반환 (유효성 검사 실패)")
    void create_user_fail_validation() throws Exception {
        // given: username을 빈 문자열("")로 세팅하여 @NotBlank 검증 실패 유도
        CreateUserRequestDto request = new CreateUserRequestDto("", "test@test.com", "password123", null);

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "request.json",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"))
            .andExpect(jsonPath("$.details.username").exists()); // username 필드에 에러가 발생했는지 확인
    }
}