package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("유저 등록 성공: 올바른 정보와 프로필 이미지를 전달하면 201 Created를 반환해야 합니다.")
  void create_success() throws Exception {
    // given
    UserDto userDto = UserDto.builder()
        .id(UUID.randomUUID())
        .email("test@email.com")
        .username("test")
        .build();

    given(userService.create(any(), any())).willReturn(userDto);

    UserCreateRequest request = new UserCreateRequest("test", "test@email.com", "test1234");

    MockMultipartFile requestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image".getBytes()
    );

    // when
    ResultActions actions = mockMvc.perform(
        multipart("/api/users")
            .file(requestPart) // JSON 데이터 파트
            .file(profilePart) // 이미지 파일 파트
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.email").value("test@email.com"));
  }

  @Test
  @DisplayName("유저 삭제 실패: 존재하지 않는 유저 ID로 삭제 요청 시 400 Bad Request를 반환해야 합니다.")
  void delete_fail() throws Exception {
    // given
    UUID id = UUID.randomUUID();

    doThrow(new UserNotFoundException(id))
        .when(userService).delete(id);

    // when
    ResultActions actions = mockMvc.perform(
        delete("/api/users/{userId}", id)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    actions.andExpect(status().isBadRequest());
    verify(userService).delete(id);
  }
}
