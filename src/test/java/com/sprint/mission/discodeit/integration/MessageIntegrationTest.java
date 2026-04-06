package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import jakarta.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private EntityManager entityManager;

  private User savedUser;
  private Channel savedChannel;

  @BeforeEach
  void setUp() {
    // 유저와 채널 생성
    savedUser = userRepository.save(new User("author", "author@test.com", "Password123", null));
    userStatusRepository.save(new UserStatus(savedUser, java.time.Instant.now()));

    savedChannel = channelRepository.save(new Channel("공개 채널", "설명", ChannelType.PUBLIC));
  }

  @Nested
  @DisplayName("POST /api/messages - 메시지 생성")
  class CreateMessage {

    @Test
    @DisplayName("성공: DB에 메시지가 성공적으로 저장된다")
    void success() throws Exception {
      // given
      MessageCreateRequest requestDto = new MessageCreateRequest("메시지", savedChannel.getId(),
          savedUser.getId());
      MockMultipartFile requestPart = new MockMultipartFile("messageCreateRequest",
          "messageCreateRequest", MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8));

      // when & then
      mockMvc.perform(
              multipart("/api/messages").file(requestPart).contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.content").value("메시지"))
          .andExpect(jsonPath("$.author.username").value("author"));

      assertThat(messageRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 채널에 작성 시도 시 예외가 발생한다")
    void fail_channelNotFound() throws Exception {
      // given
      MessageCreateRequest requestDto = new MessageCreateRequest("메시지", UUID.randomUUID(),
          savedUser.getId());
      MockMultipartFile requestPart = new MockMultipartFile("messageCreateRequest",
          "messageCreateRequest", MediaType.APPLICATION_JSON_VALUE,
          objectMapper.writeValueAsString(requestDto).getBytes(StandardCharsets.UTF_8));

      // when & then
      mockMvc.perform(
              multipart("/api/messages").file(requestPart).contentType(MediaType.MULTIPART_FORM_DATA))
          .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("GET /api/messages - 메시지 목록 조회")
  class FindAllMessages {

    @Test
    @DisplayName("성공: 채널 내의 메시지를 반환한다")
    void success() throws Exception {
      // given
      Message message = new Message("메시지 1", savedUser, savedChannel, null);
      messageRepository.save(message);

      // 캐시를 비워서 API가 강제로 쿼리를 날리도록 세팅
      entityManager.flush();
      entityManager.clear();

      // when & then
      mockMvc.perform(get("/api/messages")
              .param("channelId", savedChannel.getId().toString())
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content.length()").value(1))
          .andExpect(jsonPath("$.content[0].content").value("메시지 1"))
          .andExpect(jsonPath("$.content[0].author.username").value("author")); // 작성자 정보가 잘 왔나?
    }

    @Test
    @DisplayName("성공: 채널에 메시지가 없으면 빈 배열을 반환한다")
    void success_empty() throws Exception {
      // given: 메시지가 없는 상태

      // when & then
      mockMvc.perform(get("/api/messages").param("channelId", savedChannel.getId().toString())
              .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content.length()").value(0));
    }
  }

  @Nested
  @DisplayName("PATCH /api/messages/{messageId} - 메시지 수정")
  class UpdateMessage {

    @Test
    @DisplayName("성공: 메시지 내용이 DB에서 업데이트된다")
    void success() throws Exception {
      // given
      Message savedMessage = messageRepository.save(
          new Message("수정 전", savedUser, savedChannel, null));
      MessageUpdateRequest requestDto = new MessageUpdateRequest(savedUser.getId(), "수정 후");

      // when & then
      mockMvc.perform(patch("/api/messages/{messageId}", savedMessage.getId()).contentType(
              MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
          .andExpect(status().isOk());

      Message updatedMessage = messageRepository.findById(savedMessage.getId()).orElseThrow();
      assertThat(updatedMessage.getContent()).isEqualTo("수정 후");
    }

    @Test
    @DisplayName("실패: 작성자가 아닌 유저가 수정 요청 시 예외가 발생한다")
    void fail_notAuthor() throws Exception {
      // given
      Message savedMessage = messageRepository.save(
          new Message("수정 전", savedUser, savedChannel, null));
      MessageUpdateRequest requestDto = new MessageUpdateRequest(UUID.randomUUID(), "수정 후");

      // when & then
      mockMvc.perform(patch("/api/messages/{messageId}", savedMessage.getId()).contentType(
              MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(requestDto)))
          .andExpect(status().is4xxClientError());
    }
  }

  @Nested
  @DisplayName("DELETE /api/messages/{messageId} - 메시지 삭제")
  class DeleteMessage {

    @Test
    @DisplayName("성공: 메시지가 DB에서 지워진다")
    void success() throws Exception {
      // given
      Message savedMessage = messageRepository.save(
          new Message("삭제 대상", savedUser, savedChannel, null));

      // when & then
      mockMvc.perform(delete("/api/messages/{messageId}", savedMessage.getId()).param("requesterId",
              savedUser.getId().toString()))
          .andExpect(status().isNoContent());
      assertThat(messageRepository.findById(savedMessage.getId())).isEmpty();
    }

    @Test
    @DisplayName("실패: 작성자가 아닌 유저가 삭제 요청 시 예외가 발생한다")
    void fail_notAuthor() throws Exception {
      // given
      Message savedMessage = messageRepository.save(
          new Message("삭제 대상", savedUser, savedChannel, null));

      // when & then
      mockMvc.perform(delete("/api/messages/{messageId}", savedMessage.getId()).param("requesterId",
              UUID.randomUUID().toString()))
          .andExpect(status().is4xxClientError());
    }
  }
}
