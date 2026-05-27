package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.security.Role;
import java.time.Instant;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private BinaryContentService binaryContentService;

    private UUID userId;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userDto = new UserDto(userId, "달선", "dalsun@naver.com", null, true, Role.USER);
    }

    @Test
    @DisplayName("사용자 등록 성공")
    void join_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("달선", "dalsun@naver.com", "ekftjs123");

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        when(userService.create(any(UserCreateRequest.class), any())).thenReturn(
                userDto);

        // when
        ResultActions actions = mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("달선"))
                .andExpect(jsonPath("$.email").value("dalsun@naver.com"));

    }

    @Test
    @DisplayName("사용자 등록 실패 - 이미 존재하는 닉네임 ")
    void join_fail_already_exists_username() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("달선", "dalsun@naver.com", "ekftjs123");

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        when(userService.create(any(UserCreateRequest.class), any()))
                .thenThrow(new UserNameAlreadyExistsException("달선"));

        // when
        ResultActions actions = mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USERNAME_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void update_success() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest("new달선", "newDalsun@naver.com",
                "newnew123");
        UserDto newUserDto = new UserDto(userId, "new달선", "newDalsun@naver.com", null, true,
                Role.USER);

        when(userService.update(any(UUID.class), any(UserUpdateRequest.class),
                any())).thenReturn(newUserDto);

        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        ResultActions actions = mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                })
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("new달선"))
                .andExpect(jsonPath("$.email").value("newDalsun@naver.com"));
    }

    @Test
    @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자")
    void update_fail_user_not_found() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest("new달선", "newDalsun@naver.com",
                "newnew123");
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest"
                , ""
                , MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request));

        when(userService.update(any(UUID.class), any(UserUpdateRequest.class), any()))
                .thenThrow(new UserNotFoundException(userId));

        // when
        ResultActions actions = mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }));

        // then
        actions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void delete_success() throws Exception {
        // given
        doNothing().when(userService).delete(any(UUID.class));

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        actions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
    void delete_fail_user_not_found() throws Exception {
        // given
        doThrow(new UserNotFoundException(userId)).when(userService).delete(any(UUID.class));

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        actions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    @DisplayName("전체 사용자 조회 성공")
    void find_all_success() throws Exception {
        // given
        List<UserDto> users = List.of(
                new UserDto(UUID.randomUUID(), "달선", "dalsun@naver.com", null, true, Role.USER),
                new UserDto(UUID.randomUUID(), "달룡", "dalyong@naver.com", null, false, Role.USER)
        );
        when(userService.findAll()).thenReturn(users);

        // when
        ResultActions actions = mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("달선"))
                .andExpect(jsonPath("$[1].username").value("달룡"));
    }

    @Test
    @DisplayName("전체 사용자 조회 성공 - 사용자 없음")
    void find_all_success_empty() throws Exception {
        // given
        when(userService.findAll()).thenReturn(List.of());

        // when
        ResultActions actions = mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

    }
}
