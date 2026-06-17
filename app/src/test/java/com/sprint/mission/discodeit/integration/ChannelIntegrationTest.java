package com.sprint.mission.discodeit.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper obj;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("PUBLIC 채널 생성 API 호출 시, DB에 PUBLIC 채널이 저장되고 정보를 반환할 수 있어야 한다.")
  void should_return_response_and_save_in_db_when_create_public_channel() throws Exception {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개 채널", "공개 채널입니다.");
    String body = obj.writeValueAsString(request);

    // when
    ResultActions actions = mockMvc.perform(
        post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body)
    );

    // then
    actions.andExpect(status().isCreated())
        .andDo(print())
        .andExpect(jsonPath("$.name").value("공개 채널"))
        .andExpect(jsonPath("$.description").value("공개 채널입니다."))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
    boolean exists = channelRepository.findAll().stream()
        .anyMatch(channel -> channel.getName().equals("공개 채널"));
    assertTrue(exists);
  }

  @Test
  @DisplayName("PRIVATE 채널 생성 API 호출 시, DB에 PRIVATE 채널이 저장되고 정보를 반환할 수 있어야 한다.")
  void should_return_response_and_save_in_db_when_create_private_channel() throws Exception {
    // given
    User user1 = userRepository.save(new User("가짜 유저1", "fake1@email.com", "1234", null));
    User user2 = userRepository.save(new User("가짜 유저2", "fake2@email.com", "1234", null));
    List<UUID> participantIds = List.of(user1.getId(), user2.getId());

    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
    String body = obj.writeValueAsString(request);

    // when
    ResultActions actions = mockMvc.perform(
        post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body)
    );

    // then
    actions.andExpect(status().isCreated())
        .andDo(print())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
    boolean exists = channelRepository.findAll().stream()
        .anyMatch(channel -> channel.getType() == ChannelType.PRIVATE);
    assertTrue(exists);
  }

  @Test
  @DisplayName("PUBLIC 채널 수정 API 호출 시, DB에 수정사항이 저장되고 정보를 반환할 수 있어야 한다.")
  void should_return_response_and_save_in_db_when_update_public_channel() throws Exception {
    // given
    Channel targetChannel = channelRepository.save(
        new Channel(ChannelType.PUBLIC, "옛날 채널", "옛날 채널입니다."));
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새로운 채널", "새로운 채널입니다.");
    String body = obj.writeValueAsString(request);

    // when
    ResultActions actions = mockMvc.perform(
        patch("/api/channels/" + targetChannel.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(body)
    );

    // then
    actions.andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("새로운 채널"))
        .andExpect(jsonPath("$.description").value("새로운 채널입니다."));
    Channel updatedChannel = channelRepository.findById(targetChannel.getId()).get();
    assertEquals("새로운 채널", updatedChannel.getName());
    assertEquals("새로운 채널입니다.", updatedChannel.getDescription());
  }

  @Test
  @DisplayName("채널 삭제 API 호출 시, DB에도 삭제되어야 한다.")
  void should_delete_in_db_when_delete_channel() throws Exception {
    // given
    Channel targetChannel = channelRepository.save(
        new Channel(ChannelType.PUBLIC, "공개 채널", "공개 채널입니다."));

    // when
    mockMvc.perform(
        delete("/api/channels/" + targetChannel.getId())
            .accept(MediaType.APPLICATION_JSON)
    );

    // then
    boolean exists = channelRepository.existsById(targetChannel.getId());
    assertFalse(exists);
  }
}
