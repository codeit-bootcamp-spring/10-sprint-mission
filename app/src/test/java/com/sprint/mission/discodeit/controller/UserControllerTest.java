package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.security.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(controllers = UserController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    },
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {JwtAuthenticationFilter.class} // 만약 SecurityConfig.class도 에러를 유발하면 배열에 추가해 주세요.
        )
    })
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper obj;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("유저 생성 요청을 처리하여 응답을 반환할 수 있어야 한다.")
  void should_return_response_when_create_user() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("김코딩", "hello@hello.com", "1234");

    BinaryContentDto profileDto = new BinaryContentDto(
        UUID.randomUUID(),
        "test.png",
        1024L,
        "image/png",
        BinaryContentStatus.PROCESSING
    );

    UserDto expectedResponse = new UserDto(
        UUID.randomUUID(),
        "김코딩",
        "hello@hello.com",
        profileDto,
        true,
        Role.USER
    );

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        obj.writeValueAsBytes(request)
    );

    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "test.png",
        MediaType.IMAGE_PNG_VALUE,
        "가짜 이미지 데이터".getBytes()
    );

    given(userService.create(any(UserCreateRequest.class), any(MultipartFile.class))).willReturn(
        expectedResponse);
    // when
    ResultActions actions = mockMvc.perform(
        multipart("/api/users")
            .file(userCreateRequestPart)
            .file(profilePart)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    actions.andExpect(status().isCreated())
        .andDo(print())
        .andExpect(jsonPath("$.username").value("김코딩"))
        .andExpect(jsonPath("$.email").value("hello@hello.com"))
        .andExpect(jsonPath("$.profile").exists())
        .andExpect(jsonPath("$.profile.fileName").value("test.png"))
        .andExpect(jsonPath("$.profile.contentType").value("image/png"))
        .andExpect(jsonPath("$.profile.size").value(1024));
  }

  @Test
  @DisplayName("존재하지 않는 유저를 삭제하려고 하면 404를 반환해야 한다.")
  void should_return_404_when_delete_not_exist_user() throws Exception {
    // given
    doThrow(new UserNotFoundException())
        .when(userService).delete(any(UUID.class));

    // when
    ResultActions actions = mockMvc.perform(
        delete("/api/users/" + UUID.randomUUID())
    );

    // then
    actions.andExpect(status().isNotFound())
        .andDo(print())
        .andExpect(jsonPath("$.message").exists());
  }
}