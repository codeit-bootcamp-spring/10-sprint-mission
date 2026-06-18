package com.sprint.mission.discodeit.integration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityManager;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Autowired
  private BinaryContentStorage binaryContentStorage;

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private EntityManager em;
  @Autowired
  private ChannelRepository channelRepository;

  private void setupSecurityContext(User user) {
    UserDetails customUserDetails = new DiscodeitUserDetails(
        new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), null, true,
            user.getCreatedAt(), user.getUpdatedAt()),
        user.getPassword()
    );

    Authentication authentication = new UsernamePasswordAuthenticationToken(
        customUserDetails,
        null,
        customUserDetails.getAuthorities()
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("성공: 프로필 사진을 가진 사용자 생성 성공")
  void createUserWithProfileImageSuccess() throws Exception {
    //given
    UserCreateRequest request = new UserCreateRequest("test", "test@test.com", "test123");

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

    //when & then
    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .file(profilePart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());

    User user = userRepository.findByUsername(request.username())
        .orElseThrow();
    assertEquals(user.getEmail(), request.email());

    //프로필 사진 저장 확인
    assertEquals(user.getProfile().getFileName(), profilePart.getOriginalFilename());
    assertTrue(binaryContentRepository.findById(user.getProfile().getId()).isPresent());
    //InputStream actualStream = binaryContentStorage.get(user.getProfile().getId());
    //assertNotNull(actualStream);
    byte[] expectedBytes = "test-image-content".getBytes();
    //byte[] actualBytes = actualStream.readAllBytes();
    //assertArrayEquals(expectedBytes, actualBytes);

    //actualStream.close();
  }

  @Test
  @DisplayName("실패: 이미 존재하는 사용자 이름으로 사용자 생성 실패")
  void createUserWithExistedUserNameFailure() throws Exception {
    //given
    userRepository.save(User.create("sameName", "test@test.com", "test123", null));
    em.flush();
    em.clear();

    UserCreateRequest request = new UserCreateRequest("sameName", "test1@test.com", "password");

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    //when & then
    mockMvc.perform(multipart("/api/users")
            .file(userPart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.exceptionType").value(UserNameAlreadyExistException.class.getSimpleName()));
  }


  @Test
  @DisplayName("성공: 프로필 이미지와 사용자 이름 수정 성공(200 OK)")
  void updateUserWithProfileImageSuccess() throws Exception {
    //given
    BinaryContent oldProfile = BinaryContent.create("oldProfile", "jpg", 50L);
    User user = userRepository.save(User.create("test", "test@test.com", "test123", oldProfile));

    em.flush();
    em.clear();

    user = userRepository.findByUsername("test").orElseThrow();
    UUID userId = user.getId();
    UUID oldProfileId = oldProfile.getId();

    setupSecurityContext(user);

    UserUpdateRequest request = new UserUpdateRequest(null, "new@test.com", null);

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

    em.flush();
    em.clear();

    User updatedUser = userRepository.findById(userId).orElseThrow();

    //프로필 사진 저장 확인
    assertEquals(updatedUser.getProfile().getFileName(), profilePart.getOriginalFilename());
    assertTrue(binaryContentRepository.findById(updatedUser.getProfile().getId()).isPresent());
    //InputStream actualStream = binaryContentStorage.get(updatedUser.getProfile().getId());
    //assertNotNull(actualStream);
    byte[] expectedBytes = "test-image-content".getBytes();
    //byte[] actualBytes = actualStream.readAllBytes();
    //assertArrayEquals(expectedBytes, actualBytes);

    assertTrue(binaryContentRepository.findById(oldProfileId).isEmpty());//이전 프로필 사진 삭제 확인

    //actualStream.close();
  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디로 사용자 수정 실패(404 not found)")
  void updateUserWithWrongUserIdFailure() throws Exception {
    //given
    UUID wrongUserId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest(null, "new@test.com", null);

    User user = userRepository.save(User.create("test", "test@test.com", "test123", null));

    em.flush();
    em.clear();

    setupSecurityContext(user);
    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    //when & then
    mockMvc.perform(multipart("/api/users/{userId}", wrongUserId)
            .file(userPart)
            .with(csrf())
            .with(r -> {
              r.setMethod("PATCH");
              return r;
            }))
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.exceptionType").value(AuthorizationDeniedException.class.getSimpleName()));

  }


  @Test
  @DisplayName("성공: 사용자 삭제 성공(204 No content)")
  void deleteUserSuccess() throws Exception {
    //given
    Channel channel = Channel.create(ChannelType.PUBLIC, "testChannel", "testChannel");
    channelRepository.save(channel);

    BinaryContent profile = BinaryContent.create("profile", "jpg", 50L);
    User user = User.create("test", "test@test.com", "test123", profile);
    userRepository.save(user);

    ReadStatus readStatus = ReadStatus.create(user, channel,
        Instant.parse("2020-01-01T00:00:00.00Z"));
    readStatusRepository.save(readStatus);

    em.flush();
    em.clear();

    User myuser = userRepository.findByUsername("test").orElseThrow();
    UUID userId = myuser.getId();
    UUID profileId = profile.getId();

    assertFalse(userRepository.findById(userId).isEmpty());
    assertFalse(binaryContentRepository.findById(profileId).isEmpty());
    assertFalse(readStatusRepository.findAllByUserId(userId).isEmpty());

    em.flush();
    em.clear();
    setupSecurityContext(user);
    //when & then
    mockMvc.perform(delete("/api/users/{userId}", userId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    em.flush();
    em.clear();

    assertTrue(userRepository.findById(userId).isEmpty());
    assertTrue(binaryContentRepository.findById(profileId).isEmpty());
    assertTrue(readStatusRepository.findAllByUserId(userId).isEmpty());

  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디로 사용자 삭제 실패(404 Not found)")
  void deleteUserByWrongUserIdFailure() throws Exception {
    //given
    UUID wrongUserId = UUID.randomUUID();
    User user = userRepository.save(User.create("test", "test@test.com", "test123", null));

    em.flush();
    em.clear();

    setupSecurityContext(user);

    //when & then
    mockMvc.perform(delete("/api/users/{userId}", wrongUserId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath("$.exceptionType").value(AuthorizationDeniedException.class.getSimpleName()));

  }

  @Test
  @DisplayName("성공: 모든 사용자 정보 조회 성공(200 Ok)")
  void findAllUsersSuccess() throws Exception {
    //given
    BinaryContent profile1 = BinaryContent.create("profile1", "jpg", 50L);
    User user1 = User.create("test1", "test1@test.com", "test123", profile1);
    BinaryContent profile2 = BinaryContent.create("profile2", "jpg", 50L);
    User user2 = User.create("test2", "test2@test.com", "test123", profile2);
    BinaryContent profile3 = BinaryContent.create("profile3", "jpg", 50L);
    User user3 = User.create("tes3", "test3@test.com", "test123", profile3);
    userRepository.saveAll(List.of(user1, user2, user3));

    em.flush();
    em.clear();

    setupSecurityContext(user1);
    //when & then
    mockMvc.perform(get("/api/users")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(3));
  }

  @Test
  @DisplayName("실패: 로그인하지 않은 사용자 요청시 예외")
  void findEmptyUserList() throws Exception {

    //when & then
    mockMvc.perform(get("/api/users")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }
}
