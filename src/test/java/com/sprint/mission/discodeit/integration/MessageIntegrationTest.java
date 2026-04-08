package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MessageIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MessageRepository messageRepository;
    @Autowired private MessageService messageService;
    @Autowired private UserService userService;
    @Autowired private ChannelService channelService;
    @Autowired private EntityManager em;

    @Test
    @DisplayName("메시지 생성 통합 테스트")
    void create_message_success() throws Exception {
        // given
        UserDto author = userService.create(new CreateUserRequestDto("msgAuthor", "msg@test.com", "pass1234", null));
        ChannelDto channel = channelService.createPublic(new CreatePublicChannelRequestDto("msgChannel", "desc"));
        em.flush();
        em.clear();

        CreateMessageRequestDto request = new CreateMessageRequestDto("Integration Message", channel.id(), author.id(), null);
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", "request.json", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request)
        );

        // when
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("Integration Message"));

        // then
        List<Message> messages = messageRepository.findAll();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("Integration Message");
    }

    @Test
    @DisplayName("메시지 수정 통합 테스트")
    void update_message_success() throws Exception {
        // given
        UserDto author = userService.create(new CreateUserRequestDto("updateAuthor", "up@test.com", "pass1234", null));
        ChannelDto channel = channelService.createPublic(new CreatePublicChannelRequestDto("upChannel", "desc"));

        MessageDto savedMessage = messageService.create(
            new CreateMessageRequestDto("Old Content", channel.id(), author.id(), null), null
        );
        em.flush();
        em.clear();

        UpdateMessageRequestDto request = new UpdateMessageRequestDto("Updated Content");

        // when
        mockMvc.perform(patch("/api/messages/{messageId}", savedMessage.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        // then
        em.flush();
        em.clear();
        Message updatedMessage = messageRepository.findById(savedMessage.id()).orElseThrow();
        assertThat(updatedMessage.getContent()).isEqualTo("Updated Content");
    }

    @Test
    @DisplayName("메시지 삭제 통합 테스트")
    void delete_message_success() throws Exception {
        // given
        UserDto author = userService.create(new CreateUserRequestDto("delAuthor", "del@test.com", "pass1234", null));
        ChannelDto channel = channelService.createPublic(new CreatePublicChannelRequestDto("delChannel", "desc"));

        MessageDto savedMessage = messageService.create(
            new CreateMessageRequestDto("To Be Deleted", channel.id(), author.id(), null), null
        );
        em.flush();
        em.clear();

        // when
        mockMvc.perform(delete("/api/messages/{messageId}", savedMessage.id()))
            .andExpect(status().isNoContent());

        // then
        boolean exists = messageRepository.existsById(savedMessage.id());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("채널 내 메시지 목록 조회 통합 테스트")
    void find_all_messages_success() throws Exception {
        // given (유저, 채널, 그리고 메시지 2개 세팅)
        UserDto author = userService.create(new CreateUserRequestDto("listUser", "list@test.com", "pass1234", null));
        ChannelDto channel = channelService.createPublic(new CreatePublicChannelRequestDto("listChannel", "desc"));

        messageService.create(new CreateMessageRequestDto("Message 1", channel.id(), author.id(), null), null);
        messageService.create(new CreateMessageRequestDto("Message 2", channel.id(), author.id(), null), null);

        em.flush();
        em.clear();

        // when & then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channel.id().toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            // 메시지가 정상적으로 2개 들어있는지 검증
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("내용이 없는 메시지 생성 시 400 Bad Request가 발생한다.")
    void create_message_empty_content_exception() throws Exception {
        // given
        UserDto author = userService.create(new CreateUserRequestDto("emptyUser", "empty@test.com", "pass1234", null));
        ChannelDto channel = channelService.createPublic(new CreatePublicChannelRequestDto("emptyChannel", "desc"));
        em.flush();
        em.clear();

        // content가 null이고 첨부파일도 없는 상태
        CreateMessageRequestDto request = new CreateMessageRequestDto(null, channel.id(), author.id(), null);
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", "request.json", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest()); // 400 에러 검증
    }

    @Test
    @DisplayName("존재하지 않는 채널에 메시지 생성 시 404 Not Found가 발생한다.")
    void create_message_invalid_channel_exception() throws Exception {
        // given
        UserDto author = userService.create(new CreateUserRequestDto("fakeChUser", "fakeCh@test.com", "pass1234", null));
        UUID fakeChannelId = UUID.randomUUID(); // 가짜 채널 ID
        em.flush();
        em.clear();

        CreateMessageRequestDto request = new CreateMessageRequestDto("Hello!", fakeChannelId, author.id(), null);
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", "request.json", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()); // 404 에러 검증
    }

    @Test
    @DisplayName("존재하지 않는 작성자로 메시지 생성 시 404 Not Found가 발생한다.")
    void create_message_invalid_author_exception() throws Exception {
        // given
        ChannelDto channel = channelService.createPublic(new CreatePublicChannelRequestDto("fakeAuthChannel", "desc"));
        UUID fakeAuthorId = UUID.randomUUID(); // 가짜 유저 ID
        em.flush();
        em.clear();

        CreateMessageRequestDto request = new CreateMessageRequestDto("Hello!", channel.id(), fakeAuthorId, null);
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", "request.json", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound()); // 404 에러 검증
    }
}