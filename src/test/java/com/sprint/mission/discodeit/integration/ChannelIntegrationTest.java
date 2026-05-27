package com.sprint.mission.discodeit.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.NotAllowedInPrivateChannelException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.persistence.EntityManager;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private BinaryContentRepository binaryContentRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;
  @Autowired
  private EntityManager em;

  private UUID u1Id;
  private UUID u2Id;
  private UUID u3Id;
  private UUID c1Id;
  private UUID c2Id;
  private UUID m1Id;
  private UUID m2Id;
  private User user1;
  private User user2;
  private User user3;

  @BeforeEach
  public void setup() {
    binaryContentRepository.deleteAll();
    messageRepository.deleteAll();
    readStatusRepository.deleteAll();
    userRepository.deleteAll();
    channelRepository.deleteAll();

    BinaryContent profile1 = BinaryContent.create("profile1", "jpg", 50L);
    user1 = User.create("test1", "test1@test.com", "test123", profile1);

    BinaryContent profile2 = BinaryContent.create("profile2", "jpg", 50L);
    user2 = User.create("test2", "test2@test.com", "test123", profile2);

    BinaryContent profile3 = BinaryContent.create("profile3", "jpg", 50L);
    user3 = User.create("tes3", "test3@test.com", "test123", profile3);

    userRepository.saveAll(List.of(user1, user2, user3));

    u1Id = user1.getId();
    u2Id = user2.getId();
    u3Id = user3.getId();

    em.flush();
    em.clear();

    Channel publicChannel = Channel.create(ChannelType.PUBLIC, "public", "description");
    channelRepository.save(publicChannel);
    readStatusRepository.save(
        ReadStatus.create(user1, publicChannel, Instant.parse("2020-01-01T00:00:00.00Z")));
    readStatusRepository.save(
        ReadStatus.create(user2, publicChannel, Instant.parse("2020-01-01T00:00:00.00Z")));
    readStatusRepository.save(
        ReadStatus.create(user3, publicChannel, Instant.parse("2020-01-01T00:00:00.00Z")));

    Channel privateChannel = Channel.create(ChannelType.PRIVATE, "private", "description");
    channelRepository.save(privateChannel);
    readStatusRepository.save(
        ReadStatus.create(user2, privateChannel, Instant.parse("2020-01-04T00:00:00.00Z")));
    readStatusRepository.save(
        ReadStatus.create(user3, privateChannel, Instant.parse("2020-01-04T00:00:00.00Z")));

    c1Id = publicChannel.getId();
    c2Id = privateChannel.getId();
    em.flush();
    em.clear();

    BinaryContent bc1 = BinaryContent.create("bc1", "jpg", 50L);
    BinaryContent bc2 = BinaryContent.create("bc2", "jpg", 50L);
    BinaryContent bc3 = BinaryContent.create("bc3", "jpg", 50L);
    BinaryContent bc4 = BinaryContent.create("bc4", "jpg", 50L);
    BinaryContent bc5 = BinaryContent.create("bc5", "jpg", 50L);
    List<BinaryContent> attachments1 = List.of(bc1, bc2);
    List<BinaryContent> attachments2 = List.of(bc3);
    Message message1 = Message.create("m1", publicChannel, user1, attachments1);
    Message message2 = Message.create("m2", publicChannel, user2, attachments2);
    messageRepository.saveAll(List.of(message1, message2));
    m1Id = message1.getId();
    m2Id = message2.getId();

    em.flush();
    em.clear();
  }

  private void setupSecurityContext(User user, Role role) {
    UserDetails customUserDetails = new DiscodeitUserDetails(
        new UserDto(user.getId(), user.getUsername(), user.getEmail(), role, null, true,
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

  @Test
  @DisplayName("성공: 공개 채널 생성 성공(201 created)")
  void createPublicChannelSuccess() throws Exception {
    //given

    PublicChannelCreateRequest dto = new PublicChannelCreateRequest("channelName",
        "channelDescription");

    setupSecurityContext(user1, Role.CHANNEL_MANAGER);
    //when
    MvcResult result = mockMvc.perform(post("/api/channels/public")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andReturn();

    //then
    //채널 아이디 추출
    String content = result.getResponse().getContentAsString();
    String createdId = JsonPath.read(content, "$.id");
    UUID channelId = UUID.fromString(createdId);

    em.flush();
    em.clear();
    // 데베 저장 검증
    Channel savedChannel = channelRepository.findById(channelId).orElseThrow();
    assertEquals(ChannelType.PUBLIC, savedChannel.getType());
    assertEquals(dto.name(), savedChannel.getName());
    assertEquals(dto.description(), savedChannel.getDescription());
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdFetchUser(
        savedChannel.getId());
    assertEquals(3, readStatuses.size());
  }

  @Test
  @DisplayName("성공: 비공개 채널(멤버 2명) 생성 성공(201 created)")
  void createPrivateChannelSuccess() throws Exception {
    //given
    PrivateChannelCreateRequest dto = new PrivateChannelCreateRequest(List.of(u1Id, u2Id));

    setupSecurityContext(user1, Role.USER);

    //when
    MvcResult result = mockMvc.perform(post("/api/channels/private")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andReturn();

    //then
    //채널 아이디 추출
    String content = result.getResponse().getContentAsString();
    String createdId = JsonPath.read(content, "$.id");
    UUID channelId = UUID.fromString(createdId);

    em.flush();
    em.clear();
    // 데베 저장 검증
    Channel savedChannel = channelRepository.findById(channelId).orElseThrow();
    assertEquals(ChannelType.PRIVATE, savedChannel.getType());
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdFetchUser(
        savedChannel.getId());
    assertEquals(2, readStatuses.size());
  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디가 포함된 참여자 목록으로 비공개 채널 생성 실패(404 not found)")
  void createPrivateChannelWithNotExistedUserIdFailure() throws Exception {
    //given
    UUID wrongId = UUID.randomUUID();
    PrivateChannelCreateRequest dto = new PrivateChannelCreateRequest(List.of(u1Id, u2Id, wrongId));

    setupSecurityContext(user1, Role.USER);

    //when & then
    mockMvc.perform(post("/api/channels/private")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exceptionType").value(UserNotFoundException.class.getSimpleName()));
  }

  @Test
  @DisplayName("성공: 공개 채널 정보 수정 성공(200 ok)")
  void updatePublicChannelSuccess() throws Exception {
    //given
    PublicChannelUpdateRequest dto = new PublicChannelUpdateRequest("newChannelName",
        "newChannelDescription");

    setupSecurityContext(user1, Role.CHANNEL_MANAGER);

    //when
    mockMvc.perform(patch("/api/channels/{channelId}", c1Id)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("newChannelName"))
        .andExpect(jsonPath("$.description").value("newChannelDescription"))
        .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()));

    em.flush();
    em.clear();

    //then
    Channel savedChannel = channelRepository.findById(c1Id).orElseThrow();
    assertEquals(ChannelType.PUBLIC, savedChannel.getType());
    assertEquals(dto.name(), savedChannel.getName());
    assertEquals(dto.description(), savedChannel.getDescription());
  }

  @Test
  @DisplayName("실패: 비공개 채널 정보 수정 실패(200 ok)")
  void updatePrivateChannelFailure() throws Exception {
    //given
    PublicChannelUpdateRequest dto = new PublicChannelUpdateRequest("newChannelName",
        "newChannelDescription");

    setupSecurityContext(user1, Role.CHANNEL_MANAGER);

    //when & then
    mockMvc.perform(patch("/api/channels/{channelId}", c2Id)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.exceptionType").value(
            NotAllowedInPrivateChannelException.class.getSimpleName()));
  }

  @Test
  @DisplayName("성공: 채널 삭제 성공(204 No content)")
  void deleteChannelSuccess() throws Exception {
    //given
    setupSecurityContext(user1, Role.CHANNEL_MANAGER);

    //when
    mockMvc.perform(delete("/api/channels/{channelId}", c1Id)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    em.flush();
    em.clear();
    //then
    assertTrue(channelRepository.findById(c1Id).isEmpty());
    assertTrue(readStatusRepository.findAllByChannelIdFetchUser(c1Id).isEmpty());
    assertTrue(messageRepository.findById(m1Id).isEmpty());
    assertTrue(messageRepository.findById(m2Id).isEmpty());
  }

  @Test
  @DisplayName("실패: 유효하지 않은 채널 아이디로 채널 삭제 실패(404 Not found)")
  void deleteChannelByWrongChannelIdFailure() throws Exception {
    //given
    UUID wrongChannelId = UUID.randomUUID();

    setupSecurityContext(user1, Role.CHANNEL_MANAGER);

    //when & then
    mockMvc.perform(delete("/api/channels/{channelId}", wrongChannelId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.exceptionType").value(ChannelNotFoundException.class.getSimpleName()));

  }


  @Test
  @DisplayName("성공: 사용자 아이디로 참여 채널 목록 조회 성공(200 Ok)")
  void findChannelsByUserSuccess() throws Exception {
    //given
    setupSecurityContext(user1, Role.USER);
    //when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", String.valueOf(u2Id))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2));
  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 아이디로 참여 채널 목록 조회 실패(404 not found)")
  void findChannelsByWrongUserFailure() throws Exception {
    //given
    UUID wrongUserId = UUID.randomUUID();
    setupSecurityContext(user1, Role.USER);

    //when & then
    mockMvc.perform(get("/api/channels")
            .param("userId", String.valueOf(wrongUserId))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.exceptionType").value(
            UserNotFoundException.class.getSimpleName()));
  }
}
