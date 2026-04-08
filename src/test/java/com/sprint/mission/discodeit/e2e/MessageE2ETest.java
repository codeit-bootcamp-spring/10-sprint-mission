package com.sprint.mission.discodeit.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequestDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("메시지 생성 → 조회")
    void create_and_find_message() throws Exception {
        // given - 유저 생성
        CreateUserRequestDTO userReq =
                new CreateUserRequestDTO("msg@test.com", "msgUser", "12345678");

        MockMultipartFile userFile = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(userReq)
        );

        String userRes = mockMvc.perform(multipart("/api/users").file(userFile))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID userId = UUID.fromString(objectMapper.readTree(userRes).get("id").asText());

        // given - 채널 생성
        CreatePublicChannelRequestDTO channelReq =
                new CreatePublicChannelRequestDTO("msg-channel", "desc");

        String channelRes = mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(channelReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID channelId = UUID.fromString(objectMapper.readTree(channelRes).get("id").asText());

        // when - 메시지 생성
        CreateMessageRequestDTO msgReq =
                new CreateMessageRequestDTO("hello", channelId, userId);

        MockMultipartFile msgFile = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(msgReq)
        );

        mockMvc.perform(multipart("/api/messages").file(msgFile))
                .andExpect(status().isCreated());

        // then - 메시지 조회
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("hello"));
    }

    @Test
    @DisplayName("메시지 생성 후 삭제")
    void delete_message() throws Exception {
        // given - 유저 생성
        CreateUserRequestDTO userReq =
                new CreateUserRequestDTO("msg2@test.com", "msgUser2", "12345678");

        MockMultipartFile userFile = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(userReq)
        );

        String userRes = mockMvc.perform(multipart("/api/users").file(userFile))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID userId = UUID.fromString(objectMapper.readTree(userRes).get("id").asText());

        // given - 채널 생성
        CreatePublicChannelRequestDTO channelReq =
                new CreatePublicChannelRequestDTO("msg-channel2", "desc");

        String channelRes = mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(channelReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID channelId = UUID.fromString(objectMapper.readTree(channelRes).get("id").asText());

        // given - 메시지 생성
        CreateMessageRequestDTO msgReq =
                new CreateMessageRequestDTO("bye", channelId, userId);

        MockMultipartFile msgFile = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(msgReq)
        );

        String msgRes = mockMvc.perform(multipart("/api/messages").file(msgFile))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String messageId = objectMapper.readTree(msgRes).get("id").asText();

        // when - 삭제
        mockMvc.perform(delete("/api/messages/{id}", messageId))
                .andExpect(status().isNoContent());

        // then - 삭제 검증
        mockMvc.perform(delete("/api/messages/{id}", messageId))
                .andExpect(status().isNotFound());
    }
}