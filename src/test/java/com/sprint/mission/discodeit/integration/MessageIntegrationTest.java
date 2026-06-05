package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MessageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper obj;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("메시지 생성 API 호출 시, DB에 메시지가 저장되고 정보를 반환할 수 있어야 한다.")
  void should_return_response_and_save_in_db_when_create_message() throws Exception {
    // given
    User author = createdAndSaveUser();
    Channel channel = createdAndSaveChannel();

    MessageCreateRequest request = new MessageCreateRequest("메시지 생성 테스트", author.getId(),
        channel.getId());

    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        obj.writeValueAsBytes(request)
    );

    MockMultipartFile attachmentPart = new MockMultipartFile(
        "attachments",
        "test.png",
        MediaType.IMAGE_PNG_VALUE,
        "가짜 이미지 데이터".getBytes()
    );

    // when
    ResultActions actions = mockMvc.perform(
        multipart("/api/messages")
            .file(messagePart)
            .file(attachmentPart)
            .file(attachmentPart)
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    actions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("메시지 생성 테스트"))
        .andExpect(jsonPath("$.attachments.length()").value(2));
    List<Message> savedMessages = messageRepository.findAll();
    boolean exists = savedMessages.stream()
        .anyMatch(m -> m.getContent().equals("메시지 생성 테스트") &&
            m.getAttachments().size() == 2);
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("메시지 수정 API 호출 시, DB에 수정사항이 저장되고 정보를 반환할 수 있어야 한다.")
  void should_return_response_and_save_in_db_when_update_message() throws Exception {
    // given
    User author = createdAndSaveUser();
    Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "공개 채널", "설명"));
    Message targetMessage = messageRepository.save(new Message("기존 메시지", channel, author));
    MessageUpdateRequest request = new MessageUpdateRequest("수정된 메시지");
    String body = obj.writeValueAsString(request);

    // when
    ResultActions actions = mockMvc.perform(
        patch("/api/messages/" + targetMessage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body)
    );

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("수정된 메시지"));
    Message updatedMessage = messageRepository.findById(targetMessage.getId()).get();
    assertEquals("수정된 메시지", updatedMessage.getContent());
  }

  @Test
  @DisplayName("메시지 삭제 API 호출 시, DB에서도 삭제되어야 한다.")
  void should_delete_in_db_when_delete_message() throws Exception {
    // given
    User author = createdAndSaveUser();
    Channel channel = createdAndSaveChannel();
    Message targetMessage = messageRepository.save(new Message("삭제될 메시지", channel, author));

    // when
    mockMvc.perform(delete("/api/messages/" + targetMessage.getId()));

    // then
    boolean exists = messageRepository.existsById(targetMessage.getId());
    assertFalse(exists);
  }

  @Test
  @DisplayName("채널별 메시지 목록 조회 API 호출 시, 해당 채널의 메시지들을 반환할 수 있어야 한다.")
  void should_return_messages_when_find_all_by_channel_id() throws Exception {
    // given
    User author = createdAndSaveUser();
    Channel channel = createdAndSaveChannel();
    messageRepository.save(new Message("첫 메시지", channel, author));
    messageRepository.save(new Message("두번째 메시지", channel, author));
    messageRepository.save(new Message("세번째 메시지", channel, author));

    // when
    ResultActions actions = mockMvc.perform(
        get("/api/messages")
            .param("channelId", channel.getId().toString())
            .param("size", "2")
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.nextCursor").exists());
  }

  private Channel createdAndSaveChannel() {
    return channelRepository.save(new Channel(ChannelType.PUBLIC, "공개 채널", "공개 채널입니다."));
  }

  private User createdAndSaveUser() {
    return userRepository.save(new User("김코딩", "hello@hello.com", "1234", null));
  }
}
