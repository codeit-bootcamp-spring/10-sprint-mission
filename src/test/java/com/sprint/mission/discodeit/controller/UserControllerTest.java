package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.fixture.UserFixture;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends BaseControllerTest {
    @MockitoBean
    private UserService userService;

    public UserControllerTest() {
        super("/users");
    }

    @Test
    void find() {

    }

    @Test
    void findAll() {
    }

    @Test
    @DisplayName("유저 생성 시 201, 데이터 반환")
    void create() throws Exception {
        UUID id = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UserCreateRequest request = UserFixture.getUserCreateRequest();
        UserResponse response = UserResponse.builder()
                .userId(id)
                .username(request.username())
                .email(request.email())
                .isActive(true)
                .profileId(profileId)
                .build();
        given(userService.create(any(UserCreateRequest.class))).willReturn(response);

        mockMvc.perform(post(getUri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(id.toString()))
                .andExpect(jsonPath("$.username").value(request.username()))
                .andExpect(jsonPath("$.email").value(request.email()))
                .andExpect(jsonPath("$.isActive").isBoolean())
                .andExpect(jsonPath("$.profileId").exists());
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void updateActiveStatus() {
    }
}