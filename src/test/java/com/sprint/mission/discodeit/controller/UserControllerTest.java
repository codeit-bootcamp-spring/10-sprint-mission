package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.controller.advice.GlobalExceptionHandler;
import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UpdateUserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.global.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 생성 성공")
    void create_user_success() throws Exception {
        UUID userId = UUID.randomUUID();

        CreateUserRequestDTO request =
                new CreateUserRequestDTO("test@test.com", "test", "123456789");

        UserDto response =
                new UserDto(userId, "test", "test@test.com", null, null);

        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(userService.createUser(any(), any())).willReturn(response);

        mockMvc.perform(
                        multipart("/api/users")
                                .file(userCreateRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/users/" + userId))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @DisplayName("중복 이메일이면 유저 생성 시 409를 반환")
    void create_user_fail_duplicate_email() throws Exception {
        CreateUserRequestDTO request =
                new CreateUserRequestDTO("test@test.com", "test", "123456789");

        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(userService.createUser(any(), any()))
                .willThrow(new DuplicateResourceException(
                        ErrorCode.EMAIL_ALREADY_EXISTS,
                        Map.of("email", request.email())
                ));

        mockMvc.perform(
                        multipart("/api/users")
                                .file(userCreateRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("U202"));
    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    void update_user_success() throws Exception {
        UUID userId = UUID.randomUUID();

        UpdateUserRequestDTO request =
                new UpdateUserRequestDTO("updatedName", null, null);

        UserDto response =
                new UserDto(userId, "updatedName", "test@test.com", null, null);

        MockMultipartFile userUpdateRequest = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(userService.updateUserInfo(eq(userId), any(), any())).willReturn(response);

        mockMvc.perform(
                        multipart("/api/users/{userId}", userId)
                                .file(userUpdateRequest)
                                .with(req -> {
                                    req.setMethod("PATCH");
                                    return req;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("updatedName"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @DisplayName("존재하지 않는 유저 수정 시 404를 반환")
    void update_user_fail_not_found() throws Exception {
        UUID userId = UUID.randomUUID();

        UpdateUserRequestDTO request =
                new UpdateUserRequestDTO("updatedName", null, null);

        MockMultipartFile userUpdateRequest = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(userService.updateUserInfo(eq(userId), any(), any()))
                .willThrow(new UserNotFoundException(userId));

        mockMvc.perform(
                        multipart("/api/users/{userId}", userId)
                                .file(userUpdateRequest)
                                .with(req -> {
                                    req.setMethod("PATCH");
                                    return req;
                                })
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유저 상태 수정 성공")
    void update_user_status_success() throws Exception {
        UUID userId = UUID.randomUUID();

        UpdateUserStatusRequestDTO request =
                new UpdateUserStatusRequestDTO(Instant.parse("2026-03-30T08:00:00Z"));

        UserDto response =
                new UserDto(userId, "test", "test@test.com", null, null);

        given(userService.updateUserStatus(eq(userId), any())).willReturn(response);

        mockMvc.perform(
                        patch("/api/users/{userId}/userStatus", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void delete_user_success() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("전체 유저 조회 성공")
    void find_all_users_success() throws Exception {
        UserDto user1 = new UserDto(UUID.randomUUID(), "user1", "user1@test.com", null, null);
        UserDto user2 = new UserDto(UUID.randomUUID(), "user2", "user2@test.com", null, null);

        given(userService.findAll()).willReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].email").value("user1@test.com"))
                .andExpect(jsonPath("$[1].username").value("user2"))
                .andExpect(jsonPath("$[1].email").value("user2@test.com"));
    }

    @Test
    @DisplayName("userId로 유저 조회 성공")
    void find_user_by_id_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto response = new UserDto(userId, "test", "test@test.com", null, null);

        given(userService.findByUserId(userId)).willReturn(response);

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @DisplayName("존재하지 않는 userId로 조회 시 404를 반환")
    void find_user_by_id_fail_not_found() throws Exception {
        UUID userId = UUID.randomUUID();

        given(userService.findByUserId(userId))
                .willThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }
}