package com.sprint.mission.discodeit.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequestDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("공개 채널 생성")
    void create_public_channel() throws Exception {
        // given
        CreateUserRequestDTO userReq =
                new CreateUserRequestDTO("channel@test.com", "channelUser", "12345678");

        MockMultipartFile userFile = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(userReq)
        );

        mockMvc.perform(multipart("/api/users").file(userFile))
                .andExpect(status().isCreated());

        CreatePublicChannelRequestDTO request =
                new CreatePublicChannelRequestDTO("channel", "desc");

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("channel"))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    @DisplayName("채널 생성 후 삭제")
    void delete_channel() throws Exception {
        // given - 유저 생성 (readStatus 때문에 필요)
        CreateUserRequestDTO userReq =
                new CreateUserRequestDTO("channel@test.com", "channelUser", "12345678");

        MockMultipartFile userFile = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(userReq)
        );

        mockMvc.perform(multipart("/api/users").file(userFile))
                .andExpect(status().isCreated());

        // given - 채널 생성
        CreatePublicChannelRequestDTO request =
                new CreatePublicChannelRequestDTO("channel2", "desc");

        String response = mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String channelId = objectMapper.readTree(response).get("id").asText();

        // when - 삭제
        mockMvc.perform(delete("/api/channels/{id}", channelId))
                .andExpect(status().isNoContent());

        // then - 삭제 확인 (선택)
        mockMvc.perform(delete("/api/channels/{id}", channelId))
                .andExpect(status().isNotFound());
    }
}