package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private BasicUserStatusService userStatusService;

  @MockBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("GET /api/users/{id} 성공")
  void getUser_Success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserDto userDto = UserDto.builder()
        .id(userId)
        .username("ho")
        .email("ho@email.com")
        .online(true)
        .build();
    given(userService.getUser(userId)).willReturn(userDto);

    // when & then
    mockMvc.perform(get("/api/users/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("ho"))
        .andExpect(jsonPath("$.email").value("ho@email.com"))
        .andExpect(jsonPath("$.online").value(true));
  }

  @Test
  @DisplayName("GET /api/users/{id} 실패 - 존재하지 않는 유저")
  void getUser_Fail_UserNotFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    given(userService.getUser(userId)).willThrow(new UserNotFoundException(userId));

    // when & then
    mockMvc.perform(get("/api/users/{id}", userId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("U001"))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.details.userId").value(userId.toString()));
  }
}
