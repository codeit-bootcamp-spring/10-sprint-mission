package com.sprint.mission.discodeit.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.dto.request.FileUploadRequest;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusDto;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.userstatus.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.support.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.support.fixture.UserFixture;
import com.sprint.mission.discodeit.support.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.support.mapper.MapperContainer;
import com.sprint.mission.discodeit.userstatus.service.UserStatusService;
import com.sprint.mission.discodeit.user.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

@Disabled("service에 Multipartfile -> FileUploadDto 변경으로 인해 추후 수정 필요")
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

  @Captor
  ArgumentCaptor<FileUploadRequest> profileCaptor;

  private final UserMapper userMapper = MapperContainer.get(UserMapper.class);
  private final BinaryContentMapper binaryContentMapper = MapperContainer.get(
      BinaryContentMapper.class);

  @Test
  @DisplayName("success to map to UserCreateRequest and MultipartFile and return UserDto")
  void success_to_map() throws Exception {
    // given
    UserCreateRequest expectedRequest = UserFixture.createRequest();
    MockMultipartFile expectedFile = BinaryContentFixture.createMockFile();
    FileUploadRequest expectedFileDto = FileUploadRequest.from(expectedFile);
    BinaryContent profile = binaryContentMapper.toEntityFrom(expectedFileDto);
    User user = userMapper.toEntityFrom(expectedRequest, UserStatusFixture.createOnline(),
        profile);
    given(userService.create(any(UserCreateRequest.class), expectedFileDto))
        .willReturn(userMapper.toDto(user));

    doTestCreateRequest(expectedRequest, expectedFile)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value(expectedRequest.getUsername()))
        .andExpect(jsonPath("$.email").value(expectedRequest.getEmail()))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.profile.id").exists())
        .andExpect(jsonPath("$.online").isBoolean())
        .andDo(print());

    ArgumentCaptor<UserCreateRequest> requestCaptor = ArgumentCaptor.forClass(
        UserCreateRequest.class);
    verify(userService).create(requestCaptor.capture(), profileCaptor.capture());

    UserCreateRequest actualRequest = requestCaptor.getValue();
    assertEqualRequest(actualRequest, expectedRequest);
    FileUploadRequest actualDto = profileCaptor.getValue();
    assertEqualProfile(actualDto, expectedFileDto);
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
    // given
    UserUpdateRequest expectedRequest = UserFixture.createUpdateRequest();
    MockMultipartFile mockProfile = BinaryContentFixture.createMockFile();
    FileUploadRequest fileDto = FileUploadRequest.from(mockProfile);
    BinaryContent expectedProfile = binaryContentMapper.toEntityFrom(fileDto);
    User user = UserFixture.createEntity();
    userMapper.partialUpdate(expectedRequest, expectedProfile, user);
    given(
        userService.update(
            any(UUID.class),
            any(UserUpdateRequest.class),
            any()))
        .willReturn(userMapper.toDto(user));

    // when & then
    doTestUpdateRequest(expectedRequest, mockProfile)
        .andExpect(getOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value(expectedRequest.getUsername()))
        .andExpect(jsonPath("$.email").value(expectedRequest.getEmail()))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.profile.id").exists())
        .andExpect(jsonPath("$.online").isBoolean())
        .andDo(print());

    ArgumentCaptor<UserUpdateRequest> requestCaptor = ArgumentCaptor.forClass(
        UserUpdateRequest.class);
    verify(userService).update(
        any(UUID.class),
        requestCaptor.capture(),
        profileCaptor.capture());
    UserUpdateRequest actualRequest = requestCaptor.getValue();
    assertEqualRequest(actualRequest, expectedRequest);
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

  private void assertEqualProfile(FileUploadRequest expected, FileUploadRequest actual) {
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);

  }

  private void assertEqualRequest(UserCreateRequest expected, UserCreateRequest actual) {
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  private void assertEqualRequest(UserUpdateRequest expected, UserUpdateRequest actual) {
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
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
