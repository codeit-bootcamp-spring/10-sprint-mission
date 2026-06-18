package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser
@WebMvcTest(
    controllers = UserController.class,
    // 테스트할 때 검증 필터를 스캔 대상에서 제외
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class
    )
)
@ActiveProfiles("test")
@Tag("unit")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;
  @MockitoBean
  private BinaryContentMapper binaryContentMapper;

  @Test
  @DisplayName("성공: 프로필 이미지와 함께 사용자 등록 성공(201 created)")
  void createUserWithProfileImageSuccess() throws Exception {
    //given
    UUID userId = UUID.randomUUID();
    UserCreateRequest request = new UserCreateRequest("test", "test@test.com", "test123");
    BinaryContentDto profiledto = new BinaryContentDto(UUID.randomUUID(), "myImage", 50L, "jpg",
        BinaryContentStatus.PROCESSING);
    UserDto userDto = new UserDto(userId, "test", "test@test.com", Role.USER, profiledto, true,
        Instant.now(),
        Instant.now());

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // 프로필 생성
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "myImage",
        MediaType.IMAGE_JPEG_VALUE,
        "test-image-content".getBytes()
    );

    given(userService.create(any(), any())).willReturn(userDto);

    //when & then
    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .file(profilePart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.profile.id").exists())
        .andExpect(jsonPath("$.profile.fileName").value("myImage"));
  }

  @Test
  @DisplayName("실패: 이미 존재하는 이메일로 가입 시 실패(409 conflict)")
  void createUserFailWithDuplicateEmail() throws Exception {
    //given
    UserCreateRequest request = new UserCreateRequest("test", "duplicate@test.com", "test123");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    given(userService.create(any(), any()))
        .willThrow(new EmailAlreadyExistException());

    //when & then
    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .with(csrf()))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.exceptionType").value(EmailAlreadyExistException.class.getSimpleName()));
  }

  @Test
  @DisplayName("성공: 프로필 이미지와 함께 사용자 수정 성공(200 OK)")
  void updateUserWithProfileImageSuccess() throws Exception {
    //given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest(null, "new@test.com", null);
    BinaryContentDto profiledto = new BinaryContentDto(UUID.randomUUID(), "newImage", 50L, "jpg",
        BinaryContentStatus.PROCESSING);
    UserDto userDto = new UserDto(userId, "test", "new@test.com", Role.USER, profiledto, true,
        Instant.now(),
        Instant.now());

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // 프로필 생성
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "newImage",
        MediaType.IMAGE_JPEG_VALUE,
        "test-image-content".getBytes()
    );

    given(userService.update(eq(userId), any(), any())).willReturn(userDto);

    //when & then
    mockMvc.perform(multipart("/api/users/{userId}", userId)
            .file(userPart)
            .file(profilePart)
            .with(csrf())
            .with(r -> {
              r.setMethod("PATCH");
              return r;
            }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value("test"))
        .andExpect(jsonPath("$.email").value("new@test.com"))
        .andExpect(jsonPath("$.profile.id").exists())
        .andExpect(jsonPath("$.profile.fileName").value("newImage"));
  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디로 사용자 수정 실패(404 not found)")
  void updateUserWithWrongUserIdFailure() throws Exception {
    //given
    UUID wrongUserId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest(null, "new@test.com", null);
    BinaryContentDto profiledto = new BinaryContentDto(UUID.randomUUID(), "newImage", 50L, "jpg",
        BinaryContentStatus.PROCESSING);
    UserDto userDto = new UserDto(wrongUserId, "test", "new@test.com", Role.USER, profiledto, true,
        Instant.now(),
        Instant.now());

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // 프로필 생성
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile",
        "newImage",
        MediaType.IMAGE_JPEG_VALUE,
        "test-image-content".getBytes()
    );

    given(userService.update(eq(wrongUserId), any(), any())).willThrow(new UserNotFoundException());

    //when & then
    mockMvc.perform(multipart("/api/users/{userId}", wrongUserId)
            .file(userPart)
            .file(profilePart)
            .with(csrf())
            .with(r -> {
              r.setMethod("PATCH");
              return r;
            }))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exceptionType").value(UserNotFoundException.class.getSimpleName()));
  }

  @Test
  @DisplayName("성공: 사용자 삭제 성공(204 No content)")
  void deleteUserSuccess() throws Exception {
    //given
    UUID userId = UUID.randomUUID();

    //when & then
    mockMvc.perform(delete("/api/users/{userId}", userId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    then(userService).should(times(1)).delete(userId);
  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디로 사용자 삭제 실패(404 Not found)")
  void deleteUserByWrongUserIdFailure() throws Exception {
    //given
    UUID wrongUserId = UUID.randomUUID();
    willThrow(new UserNotFoundException()).given(userService).delete(eq(wrongUserId));

    //when & then
    mockMvc.perform(delete("/api/users/{userId}", wrongUserId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exceptionType").value(UserNotFoundException.class.getSimpleName()));

  }

  @Test
  @DisplayName("성공: 모든 사용자 정보 조회 성공(200 Ok)")
  void findAllUsersSuccess() throws Exception {
    //given
    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    UUID userId3 = UUID.randomUUID();
    UserDto userDto1 = new UserDto(userId1, "test1", "test1@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    UserDto userDto2 = new UserDto(userId2, "test2", "test2@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    UserDto userDto3 = new UserDto(userId3, "test3", "test3@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    List<UserDto> users = List.of(userDto1, userDto2, userDto3);

    given(userService.findAll()).willReturn(users);

    //when & then
    mockMvc.perform(get("/api/users")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(users.size()))
        .andExpect(jsonPath("$[0].id").value(userId1.toString()))
        .andExpect(jsonPath("$[1].id").value(userId2.toString()))
        .andExpect(jsonPath("$[2].id").value(userId3.toString()));

  }

  @Test
  @DisplayName("성공: 사용자가 없을 때 빈 리스트 반환 성공(200 Ok)")
  void findEmptyUserList() throws Exception {
    //given
    given(userService.findAll()).willReturn(List.of());

    //when & then
    mockMvc.perform(get("/api/users")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(0));
  }

}