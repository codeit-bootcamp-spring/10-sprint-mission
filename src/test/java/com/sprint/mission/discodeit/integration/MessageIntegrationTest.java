package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.message.MessageDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@WithMockUser(username = "yushi", roles = "CHANNEL_MANAGER")        // 통합 테스트 내 채널 생성 로직 존재
public class MessageIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtRegistry jwtRegistry;

    /*
        메시지 생성
     */
    // [성공]
    @Test
    @DisplayName("메시지 생성 성공")
    void create_message_success() throws Exception {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        UserDto author = userService.create(userCreateRequest, null);

        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest(
                "Alien",
                "To be Alien together"
        );
        ChannelDto channel = channelService.createPublicChannel(channelCreateRequest);

        // 생성할 메시지
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                "LUV ME HATE ME",
                author.id(),
                channel.id()
        );

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(messageCreateRequest).getBytes()
        );

        // when
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER")))
                .andExpect(status().isCreated());

        // then
        List<MessageEntity> messages = messageRepository.findAll();
        assertEquals(messageCreateRequest.content(), messages.get(0).getContent());
    }

    // [실패]
    @Test
    @DisplayName("메시지 생성 실패: 채널 ID가 누락된 경우, 400 Bad Request 반환")
    void create_message_failure() throws Exception {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        UserDto author = userService.create(userCreateRequest, null);

        // 생성할 메시지
        MessageCreateRequest MessageCreateRequest = new MessageCreateRequest(
                "LUV ME HATE ME",
                author.id(),
                null
        );

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(MessageCreateRequest).getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    /*
        메시지 수정
     */
    // [성공]
    @Test
    @DisplayName("메시지 수정 성공")
    void update_message_success() throws Exception {
        // given

        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        UserDto author = userService.create(userCreateRequest, null);

        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest(
                "Alien",
                "To be Alien together"
        );
        ChannelDto channel = channelService.createPublicChannel(channelCreateRequest);

        // 기존 메시지 정보
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                "LUV ME HATE ME",
                author.id(),
                channel.id()
        );
        MessageDto savedMessage = messageService.create(messageCreateRequest, null);

        // 수정할 메시지
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
                "KILL ME KILL ME"
        );

        // when
        mockMvc.perform(patch("/api/messages/{messageId}", savedMessage.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageUpdateRequest))
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER")))
                .andExpect(status().isOk());

        // then
        MessageEntity updatedMessage = messageRepository.findById(savedMessage.id()).orElseThrow();
        assertEquals(messageUpdateRequest.newContent(), updatedMessage.getContent());
    }

    // [실패]
    @Test
    @DisplayName("메시지 수정 실패: 메시지 ID가 잘못된 경우, 400 Bad Request 반환")
    void update_message_failure() throws Exception {
        // given
        String messageId = "messageId";

        // 수정할 메시지
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
                "KILL ME KILL ME"
        );

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageUpdateRequest))
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentTypeMismatchException"));
    }

    /*
        메시지 삭제
     */
    // [성공]
    @Test
    @DisplayName("메시지 삭제 완료")
    void delete_message_success() throws Exception {
        // given

        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        UserDto author = userService.create(userCreateRequest, null);

        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest(
                "Alien",
                "To be Alien together"
        );
        ChannelDto channel = channelService.createPublicChannel(channelCreateRequest);

        // 삭제할 사용자
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                "LUV ME HATE ME",
                author.id(),
                channel.id()
        );
        MessageDto savedMessage = messageService.create(messageCreateRequest, null);

        // when
        mockMvc.perform(delete("/api/messages/{messageId}", savedMessage.id())
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER")))
                .andExpect(status().isNoContent());

        // then
        boolean exists = messageRepository.existsById(savedMessage.id());
        assertFalse(exists);
    }

    // [실패]
    @Test
    @DisplayName("HTTP 메서드가 잘못된 경우, 405 Method Not Allowed 반환")
    void delete_message_failure() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        // when & then
        mockMvc.perform(post("/api/messages/{messageId}", messageId)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.exceptionType").value("HttpRequestMethodNotSupportedException"));
    }

    /*
        특정 채널에서 발행된 목록 조회
     */
    // [성공]
    @Test
    @DisplayName("특정 채널에서 발행한 목록 조회 완료")
    void find_all_message_by_channel_id_success() throws Exception {
        // given

        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        UserDto author = userService.create(userCreateRequest, null);

        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest(
                "Alien",
                "To be Alien together"
        );
        ChannelDto channel = channelService.createPublicChannel(channelCreateRequest);

        //
        MessageCreateRequest firstMessageCreateRequest = new MessageCreateRequest(
                "LUV ME HATE ME",
                author.id(),
                channel.id()
        );
        MessageDto firstMessage = messageService.create(firstMessageCreateRequest, null);

        MessageCreateRequest secondMessageCreateRequest = new MessageCreateRequest(
                "KILL ME KILL ME",
                author.id(),
                channel.id()
        );
        MessageDto secondMessage = messageService.create(secondMessageCreateRequest, null);

        // when & then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.id().toString())
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("yushi").roles("CHANNEL_MANAGER")))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))

                .andExpect(jsonPath("$.content[0].content").value("KILL ME KILL ME"))

                .andExpect(jsonPath("$.hasNext").value(true));
    }

    // [실패]
    @Test
    @DisplayName("특정 채널에서 발행한 목록 조회 실패: 특정 채널 ID가 누락된 경우, 400 Bad Request 반환")
    void find_all_message_by_channel_id_failure() throws Exception {
        // given

        // when
        mockMvc.perform(get("/api/messages")
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MissingServletRequestParameterException"));
    }
}
