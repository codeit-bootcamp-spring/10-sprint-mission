package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper obj;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("유저 생성 API 호출 시, DB에 유저가 저장되고 정보를 반환할 수 있어야 한다.")
  @WithMockUser(username = "testUser", roles = "ADMIN")
  void should_return_response_and_save_in_db_when_create_user() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("김코딩", "hello@hello.com", "1234");
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "test.png",
        MediaType.IMAGE_PNG_VALUE,
        "가짜 이미지 데이터".getBytes()
    );
    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        obj.writeValueAsBytes(request)
    );

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
        .andExpect(jsonPath("$.email").value("hello@hello.com"));
    boolean exists = userRepository.existsByEmail("hello@hello.com");
    assertTrue(exists);
  }

  @Test
  @DisplayName("유저 수정 API 호출 시, DB에 수정사항이 저장되고 정보를 반환할 수 있어야 한다.")
  @WithMockUser(username = "testUser", roles = "ADMIN")
  void should_return_response_and_save_in_db_when_update_user() throws Exception {
    // given
    User savedUser = userRepository.save(new User("구코딩", "old@hello.com", "1234", null));
    UUID targetId = savedUser.getId();

    UserUpdateRequest request = new UserUpdateRequest("김코딩", "hello@hello.com", null);
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "test.png",
        MediaType.IMAGE_PNG_VALUE,
        "가짜 이미지 데이터".getBytes()
    );
    MockMultipartFile userUpdateRequest = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        obj.writeValueAsBytes(request)
    );

    UserDto userDto = new UserDto(
        savedUser.getId(),
        savedUser.getUsername(),
        savedUser.getEmail(),
        null,
        true,
        savedUser.getRole()
    );
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, savedUser.getPassword());
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails,
        userDetails.getPassword(),
        userDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // when
    ResultActions actions = mockMvc.perform(
        multipart(HttpMethod.PATCH, "/api/users/" + targetId)
            .file(userUpdateRequest)
            .file(profilePart)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    actions.andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.username").value("김코딩"))
        .andExpect(jsonPath("$.email").value("hello@hello.com"));
    User updatedUser = userRepository.findById(targetId).get();
    assertEquals("김코딩", updatedUser.getUsername());
    assertEquals("hello@hello.com", updatedUser.getEmail());
    assertNotNull(updatedUser.getProfile());
  }

  @Test
  @DisplayName("유저 목록 조회 API 호출 시, 유저 정보를 반환할 수 있어야 한다.")
  @WithMockUser(username = "testUser", roles = "ADMIN")
  void should_return_response_when_find_all_user() throws Exception {
    // given
    User user1 = new User("김코딩", "hello@hello.com", "1234", null);
    User user2 = new User("이코딩", "hi@hi.com", "1234", null);
    userRepository.save(user1);
    userRepository.save(user2);

    // when
    ResultActions actions = mockMvc.perform(
        get("/api/users")
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    actions.andExpect(status().isOk())
        .andDo(print())
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[*].username").value(hasItems("admin", "김코딩", "이코딩")))
        .andExpect(jsonPath("$[*].email").value(hasItems("test@test.com", "hello@hello.com", "hi@hi.com")));
    long userCount = userRepository.count();
    assertThat(userCount).isEqualTo(3);
  }

  @Test
  @DisplayName("유저 삭제 API 호출 시, DB에도 삭제되어야 한다.")
  void should_delete_in_db_when_delete_user() throws Exception {
    // given
    User targetUser = userRepository.save(new User("김코딩", "hello@hello.com", "1234", null));

    UserDto userDto = new UserDto(
        targetUser.getId(),
        targetUser.getUsername(),
        targetUser.getEmail(),
        null,
        true,
        targetUser.getRole()
    );
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, targetUser.getPassword());
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails,
        userDetails.getPassword(),
        userDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // when
    ResultActions actions = mockMvc.perform(
        delete("/api/users/" + targetUser.getId())
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    boolean exists = userRepository.existsById(targetUser.getId());
    assertFalse(exists);
  }

}
