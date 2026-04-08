package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.mapper.BinaryContentMapperImpl;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserMapperImpl;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserStatusService userStatusService;

  @Autowired
  private ObjectMapper objectMapper;

  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    userMapper = new UserMapperImpl(new BinaryContentMapperImpl());
  }

  @Test
  @DisplayName("success to map to UserCreateRequest and MultipartFile and return UserDto")
  void success_to_map() throws Exception {
    UserCreateRequest actualRequest = UserFixture.createRequest();
    MockMultipartFile actualProfile = BinaryContentFixture.createMockFile();
    User user = userMapper.toEntityFrom(actualRequest, UserStatusFixture.createOnline(),
        BinaryContentFixture.createEntity());
    given(userService.create(any(UserCreateRequest.class), any(MultipartFile.class)))
        .willReturn(userMapper.toDto(user));

    doTestCreateRequest(actualRequest, actualProfile)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value(actualRequest.getUsername()))
        .andExpect(jsonPath("$.email").value(actualRequest.getEmail()))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.profile.id").exists())
        .andExpect(jsonPath("$.online").isBoolean())
        .andDo(print());

    ArgumentCaptor<UserCreateRequest> requestCaptor = ArgumentCaptor.forClass(
        UserCreateRequest.class);
    ArgumentCaptor<MultipartFile> profileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
    verify(userService).create(requestCaptor.capture(), profileCaptor.capture());

    UserCreateRequest expectedRequest = requestCaptor.getValue();
    assertEqualRequest(expectedRequest, actualRequest);
    MultipartFile expectedProfile = profileCaptor.getValue();
    assertEqualProfile(expectedProfile, actualProfile);
  }

  @ParameterizedTest
  @MethodSource("provideInvalidCreateRequests")
  @DisplayName("fail to map UserCreateRequest due to containing whitespace")
  void fail_to_map_UserCreateRequest(UserCreateRequest request) throws Exception {
    MockMultipartFile actualProfile = BinaryContentFixture.createMockFile();
    doTestCreateRequest(request, actualProfile)
        .andExpect(getBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("success to map to UserUpdateRequest")
  void success_to_map_userUpdateRequest() throws Exception {
    UserUpdateRequest actualRequest = UserFixture.createUpdate();
    BinaryContent profile = BinaryContentFixture.createEntity();
    MockMultipartFile mockProfile = BinaryContentFixture.toMockFile(profile);
    User user = User.builder()
        .status(UserStatusFixture.createOnline())
        .build();
    userMapper.partialUpdate(actualRequest, profile, user);
    given(
        userService.update(any(UUID.class), any(UserUpdateRequest.class), any(MultipartFile.class)))
        .willReturn(userMapper.toDto(user));

    doTestUpdateRequest(actualRequest, mockProfile)
        .andExpect(getOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value(actualRequest.getUsername()))
        .andExpect(jsonPath("$.email").value(actualRequest.getEmail()))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.profile.id").exists())
        .andExpect(jsonPath("$.online").isBoolean())
        .andDo(print());

    ArgumentCaptor<UserUpdateRequest> requestCaptor = ArgumentCaptor.forClass(
        UserUpdateRequest.class);
    verify(userService).update(any(UUID.class), requestCaptor.capture(), any(MultipartFile.class));
    UserUpdateRequest expectedRequest = requestCaptor.getValue();
    assertEqualRequest(expectedRequest, actualRequest);
  }

  @ParameterizedTest(name = "fail to map to UserUpdateRequest due to containing whitespace")
  @MethodSource("provideInvalidUpdateRequests")
  void fail_to_map_userUpdateRequest(UserUpdateRequest request) throws Exception {
    MockMultipartFile profile = BinaryContentFixture.createMockFile();
    doTestUpdateRequest(request, profile)
        .andExpect(getBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("success to map to UserStatusUpdateRequest with LocalDateTime")
  void success_to_map_userStatusUpdateRequest() throws Exception {
    UserStatusUpdateRequest requestWithInstant = UserStatusFixture.createUpdate();
    given(userStatusService.update(any(UUID.class), any(UserStatusUpdateRequest.class)))
        .willReturn(new UserStatusDto(null, null, requestWithInstant.getLastActiveAt()));

    doTestUserStatusUpdate(requestWithInstant)
        .andExpect(getOk())
        .andExpect(
            jsonPath("$.lastActiveAt").value(requestWithInstant.getLastActiveAt().toString()))
        .andDo(print());
  }

  @ParameterizedTest(name = "fail to map to UserStatusUpdateRequest due to empty or whitespace: [{0}]")
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   ", "\t", "\n"})
  void fail_to_map_userStatusUpdateRequest(String invalidValue) throws Exception {
    Map<String, String> request = new HashMap<>();
    request.put("lastActiveAt", invalidValue);
    doTestUserStatusUpdate(request)
        .andExpect(getBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("fail to map to UserStatusUpdateRequest due to receiving empty body")
  void fail_to_map_userStatusUpdateRequest_with_empty_body() throws Exception {
    Map<String, Object> emptyRequest = Collections.emptyMap();
    doTestUserStatusUpdate(emptyRequest)
        .andExpect(getBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("fail to map to UserStatusUpdateRequest due to receiving no content")
  void fail_to_map_userStatusUpdateRequest_without_content() throws Exception {
    mockMvc.perform(
            patch("/api/users/{userId}/userStatus", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(getBadRequest())
        .andDo(print());
  }

  private ResultActions doTestUserStatusUpdate(Object request) throws Exception {
    String content = objectMapper.writeValueAsString(request);
    return mockMvc.perform(
        patch("/api/users/{userId}/userStatus", UUID.randomUUID())
            .content(content)
            .contentType(MediaType.APPLICATION_JSON));
  }

  private MockMultipartFile getUserPart(Object request, String name) throws Exception {
    String content = objectMapper.writeValueAsString(request);
    return new MockMultipartFile(
        name,
        "",
        MediaType.APPLICATION_JSON_VALUE,
        content.getBytes(StandardCharsets.UTF_8));
  }

  private void assertEqualProfile(MultipartFile expected, MockMultipartFile actual) {
    Assertions.assertThat(expected)
        .extracting(MultipartFile::getName, MultipartFile::getSize, MultipartFile::getContentType)
        .containsExactly(actual.getName(), actual.getSize(), actual.getContentType());
  }

  private void assertEqualRequest(UserCreateRequest expected, UserCreateRequest actual) {
    Assertions.assertThat(expected)
        .extracting("username", "email", "password")
        .containsExactly(actual.getUsername(), actual.getEmail(), actual.getPassword());
  }

  private void assertEqualRequest(UserUpdateRequest expected, UserUpdateRequest actual) {
    Assertions.assertThat(expected)
        .extracting("username", "email", "password")
        .containsExactly(actual.getUsername(), actual.getEmail(), actual.getPassword());
  }

  private ResultActions doTestCreateRequest(UserCreateRequest request, MockMultipartFile profile)
      throws Exception {
    MockMultipartFile userPart = getUserPart(request, "userCreateRequest");

    return mockMvc.perform(
        multipart("/api/users")
            .file(userPart)
            .file(profile)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  private ResultActions doTestUpdateRequest(UserUpdateRequest request, MockMultipartFile profile)
      throws Exception {
    MockMultipartFile userPart = getUserPart(request, "userUpdateRequest");
    return mockMvc.perform(
        multipart(HttpMethod.PATCH, "/api/users/{id}", UUID.randomUUID())
            .file(userPart)
            .file(profile)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.MULTIPART_FORM_DATA));
  }

  private static Stream<UserUpdateRequest> provideInvalidUpdateRequests() {
    return Stream.of(
        new UserUpdateRequest("  ", "asd@asd.com", "bad"),
        new UserUpdateRequest("asd asd", "asd@asd.com", "bad"),
        new UserUpdateRequest("asd asd", "asdasd.com", "bad"),
        new UserUpdateRequest("asd asd", "asda@sd.com", "b dv s"),
        new UserUpdateRequest(null, null, "cs s23dx")
    );
  }

  private static Stream<UserCreateRequest> provideInvalidCreateRequests() {
    return Stream.of(
        new UserCreateRequest("l e e e", "abda@b.com", "bad"),
        new UserCreateRequest("   ", "abda@b.com", "bad"),
        new UserCreateRequest("leee", "abdabcom", "bad"),
        new UserCreateRequest("leee", "abd@abc.om", "ba d as ")
    );
  }

  private ResultMatcher getOk() {
    return status().isOk();
  }

  private ResultMatcher getBadRequest() {
    return status().isBadRequest();
  }
}
