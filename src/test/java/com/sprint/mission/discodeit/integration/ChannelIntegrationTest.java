package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChannelIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Test
    @DisplayName("PUBLIC 채널 생성 성공")
    void createPublicChannel_success() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "전체 공지");

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("공지"));

        assertThat(channelRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("PRIVATE 채널 생성 성공")
    void createPrivateChannel_success() throws Exception {
        User user1 = userRepository.save(new User("user1", "1234", "user1@test.com"));
        User user2 = userRepository.save(new User("user2", "1234", "user2@test.com"));
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(Set.of(user1.getId(), user2.getId()));

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PRIVATE"))
                .andExpect(jsonPath("$.participants.length()").value(2));

        assertThat(readStatusRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("PUBLIC 채널 정보 수정 성공")
    void updateChannelInfo_success() throws Exception {
        Channel channel = channelRepository.save(Channel.buildPublic("before", "desc"));
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("after", "updated");

        mockMvc.perform(patch("/api/channels/{channelId}", channel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("after"))
                .andExpect(jsonPath("$.description").value("updated"));

        Channel updated = channelRepository.findById(channel.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("after");
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel_success() throws Exception {
        Channel channel = channelRepository.save(Channel.buildPublic("delete", "desc"));
        UUID channelId = channel.getId();

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());

        assertThat(channelRepository.findById(channelId)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 채널 수정 실패 - 404")
    void updateChannel_fail_notFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new", "desc");

        mockMvc.perform(patch("/api/channels/{channelId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("ChannelNotFoundException"));
    }

    @Test
    @DisplayName("PUBLIC 채널 생성 실패 - 유효성 검증 (빈 이름)")
    void createPublicChannel_fail_validation() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "desc");

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("존재하지 않는 채널 삭제 실패 - 404")
    void deleteChannel_fail_notFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/channels/{channelId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }
}
