package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    @MockitoBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    @DisplayName("User 생성 성공")
    void createUser_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UserCreateRequest request = new UserCreateRequest("dustle", "dustle@test.com", "1234");
        UserDto response = new UserDto(userId, "dustle", "dustle@test.com", null, null);

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "image".getBytes()
        );

        given(userService.createUser(org.mockito.ArgumentMatchers.eq(request), org.mockito.ArgumentMatchers.any()))
                .willReturn(response);

        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .file(profile))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("dustle"))
                .andExpect(jsonPath("$.email").value("dustle@test.com"));
    }

    @Test
    @DisplayName("User 단건 조회 성공")
    void findUserByUserId_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDto response = new UserDto(userId, "dustle", "dustle@test.com", null, true);
        given(userService.findUserByUserID(userId)).willReturn(response);

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(header().exists("Discodeit-Request-ID"))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @DisplayName("존재하지 않는 User 조회 실패")
    void findUserByUserId_fail_notFound() throws Exception {
        UUID userId = UUID.randomUUID();
        given(userService.findUserByUserID(userId)).willThrow(new UserNotFoundException(
                "사용자를 찾을 수 없습니다 userId: " + userId,
                Map.of("userId", userId)
        ));

        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("UserNotFoundException"))
                .andExpect(jsonPath("$.details.userId").value(userId.toString()));
    }
}
