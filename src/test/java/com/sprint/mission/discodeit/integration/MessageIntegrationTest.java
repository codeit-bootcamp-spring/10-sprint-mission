package com.sprint.mission.discodeit.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.InvalidMessageException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
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
import org.springframework.mock.web.MockMultipartFile;
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
public class MessageIntegrationTest {

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
    Message message3 = Message.create("m3", publicChannel, user3, attachments1);
    messageRepository.saveAll(List.of(message1, message2, message3));
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
  @DisplayName("성공: 첨부파일과 함께 메세지 전송 성공(201 created)")
  void sendMessageWithAttachmentsSuccess() throws Exception {
    //given
    setupSecurityContext(user1, Role.USER);
    MessageCreateRequest request = new MessageCreateRequest("testContent", c1Id, u1Id);

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    // 첨부파일 생성
    // 첨부파일 1
    MockMultipartFile attachmentsPart1 = new MockMultipartFile(
        "attachments",
        "myImage",
        MediaType.IMAGE_JPEG_VALUE,
        "test-image-content".getBytes()
    );
    // 첨부파일 2
    MockMultipartFile attachmentsPart2 = new MockMultipartFile(
        "attachments",
        "myTxt.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "text-content".getBytes()
    );

    //when
    MvcResult result = mockMvc.perform(multipart("/api/messages")
            .file(userPart)
            .file(attachmentsPart1)
            .file(attachmentsPart2)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    //then
    String content = result.getResponse().getContentAsString();
    String createdId = JsonPath.read(content, "$.id");
    UUID newMessageId = UUID.fromString(createdId);

    em.flush();
    em.clear();

    Message message = messageRepository.findById(newMessageId).orElseThrow();
    assertEquals(2, message.getAttachments().size());
    assertEquals(request.content(), message.getContent());
    assertEquals(u1Id, message.getAuthor().getId());
    assertEquals(c1Id, message.getChannel().getId());
  }

  @Test
  @DisplayName("실패: 빈 메세지 전송 실패(400 bad request)")
  void sendMessageWithNoContentFailure() throws Exception {
    //given
    setupSecurityContext(user1, Role.USER);
    MessageCreateRequest request = new MessageCreateRequest("", c1Id, u1Id);

    // 바디 생성
    MockMultipartFile userPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request)
    );

    //when & then
    mockMvc.perform(multipart("/api/messages")
            .file(userPart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.exceptionType").value(InvalidMessageException.class.getSimpleName()));
  }


  @Test
  @DisplayName("성공: 메세지 내용 변경 성공(200 ok)")
  void updateMessageContentSuccess() throws Exception {
    //given
    setupSecurityContext(user1, Role.USER);
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");

    //when & then
    mockMvc.perform(patch("/api/messages/{messageId}", m1Id)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(m1Id.toString()))
        .andExpect(jsonPath("$.content").value(request.newContent()));

    em.flush();
    em.clear();

    Message message = messageRepository.findById(m1Id).orElseThrow();
    assertEquals(request.newContent(), message.getContent());
  }

  @Test
  @DisplayName("실패: 유효하지 않은 메세지 아이디에 대해 메세지 내용 변경 실패(404 not found)")
  void updateMessageContentWithNotExistedMessageIdFailure() throws Exception {
    //given
    setupSecurityContext(user1, Role.USER);
    UUID wrongMessageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("newContent");

    //when & then
    mockMvc.perform(patch("/api/messages/{messageId}", wrongMessageId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.exceptionType").value(MessageNotFoundException.class.getSimpleName()));
  }


  @Test
  @DisplayName("성공: 메세지 삭제 성공(204 No content)")
  void deleteMessageSuccess() throws Exception {
    setupSecurityContext(user1, Role.USER);
    //when & then
    mockMvc.perform(delete("/api/messages/{messageId}", m1Id)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    em.flush();
    em.clear();

    assertTrue(messageRepository.findById(m1Id).isEmpty());
  }

  @Test
  @DisplayName("실패: 유효하지 않은 메세지 아이디로 사용자 삭제 실패(404 Not found)")
  void deleteMessageByWrongUserIdFailure() throws Exception {
    //given
    UUID wrongMessageId = UUID.randomUUID();
    setupSecurityContext(user1, Role.USER);

    //when & then
    mockMvc.perform(delete("/api/messages/{messageId}", wrongMessageId)
            .with(csrf())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.exceptionType").value(MessageNotFoundException.class.getSimpleName()));

  }


  @Test
  @DisplayName("성공: 채널 아이디로 페이지네이션이 적용된 메세지 목록 조회 성공")
  void findMessagesByChannelIdSuccess() throws Exception {
    //given
    setupSecurityContext(user1, Role.USER);
    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", c1Id.toString())
            .param("size", "2")
            .param("sort", "createdAt,desc")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(2))
        .andExpect(jsonPath("$.hasNext").value(true));
  }

  @Test
  @DisplayName("성공: 페이지네이션 기본 값으로 채널 아이디로 페이지네이션이 적용된 메세지 목록 조회 성공")
  void findMessagesByChannelIdWithDefaultPaginationSuccess() throws Exception {
    //given
    setupSecurityContext(user1, Role.USER);
    // when & then
    mockMvc.perform(get("/api/messages")
            .param("channelId", c1Id.toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.size()").value(3))//기본 50
        .andExpect(jsonPath("$.hasNext").value(false));
  }
}
