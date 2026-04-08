package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_success() throws Exception {
        User user = userRepository.save(new User("author", "1234", "author@test.com"));
        Channel channel = channelRepository.save(Channel.buildPublic("general", "desc"));
        MessageCreateRequest request = new MessageCreateRequest("hello", channel.getId(), user.getId());
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages").file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("hello"))
                .andExpect(jsonPath("$.channelId").value(channel.getId().toString()));

        assertThat(messageRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage_success() throws Exception {
        User user = userRepository.save(new User("author", "1234", "author@test.com"));
        Channel channel = channelRepository.save(Channel.buildPublic("general", "desc"));
        Message message = messageRepository.save(new Message(user, channel, "before"));

        mockMvc.perform(patch("/api/messages/{messageId}", message.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MessageUpdateRequest("after"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("after"));

        assertThat(messageRepository.findById(message.getId()).orElseThrow().getContent()).isEqualTo("after");
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage_success() throws Exception {
        User user = userRepository.save(new User("author", "1234", "author@test.com"));
        Channel channel = channelRepository.save(Channel.buildPublic("general", "desc"));
        Message message = messageRepository.save(new Message(user, channel, "delete me"));
        UUID messageId = message.getId();

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());

        assertThat(messageRepository.findById(messageId)).isEmpty();
    }

    @Test
    @DisplayName("메시지 목록 조회 성공")
    void findAllMessagesByChannelId_success() throws Exception {
        User user = userRepository.save(new User("author", "1234", "author@test.com"));
        Channel channel = channelRepository.save(Channel.buildPublic("general", "desc"));
        Message older = persistMessage(user, channel, "older", Instant.parse("2026-04-01T00:00:00Z"));
        Message newer = persistMessage(user, channel, "newer", Instant.parse("2026-04-01T00:01:00Z"));
        flushAndClear();

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString())
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(newer.getId().toString()))
                .andExpect(jsonPath("$.content[1].id").value(older.getId().toString()));
    }

    private Message persistMessage(User user, Channel channel, String content, Instant createdAt) {
        Message message = new Message(user, channel, content);
        messageRepository.save(message);
        entityManager.flush();
        ReflectionTestUtils.setField(message, "createdAt", createdAt);
        entityManager.createNativeQuery("update messages set created_at = ? where id = ?")
                .setParameter(1, createdAt)
                .setParameter(2, message.getId())
                .executeUpdate();
        return message;
    }

    @Test
    @DisplayName("존재하지 않는 메시지 수정 실패 - 404")
    void updateMessage_fail_notFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(patch("/api/messages/{messageId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MessageUpdateRequest("updated"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("MessageNotFoundException"));
    }

    @Test
    @DisplayName("존재하지 않는 메시지 삭제 실패 - 404")
    void deleteMessage_fail_notFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/messages/{messageId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 사용자")
    void createMessage_fail_userNotFound() throws Exception {
        Channel channel = channelRepository.save(Channel.buildPublic("general", "desc"));
        MessageCreateRequest request = new MessageCreateRequest("hello", channel.getId(), UUID.randomUUID());
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/messages").file(requestPart))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
