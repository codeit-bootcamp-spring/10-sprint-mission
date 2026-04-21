package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.type.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ChannelIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    EntityManager em;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;

    @Test
    @DisplayName("공용 채널을 생성하면 실제 DB에 채널이 저장되어야 한다")
    void createPublicChannel_integration_success() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("통합테스트방", "다드루와");

        // when
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // then
        List<Channel> channels = channelRepository.findAll();
        assertThat(channels).hasSize(1);
        assertThat(channels.get(0).getName()).isEqualTo("통합테스트방");
    }

    @Test
    @DisplayName("개인 채널을 생성하면 실제 DB에 채널이 저장되어야 한다")
    void createPrivateChannel_integration_success() throws Exception {
        // given
        User user1 = userRepository.save(new User("김현재", "fred@naver.com","123123"));
        UUID userId = user1.getId();
        UserStatus userStatus = userStatusRepository.save(new UserStatus(user1));

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId));

        // when
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // then
        List<Channel> channels = channelRepository.findAll();
        assertThat(channels).hasSize(1);
    }

    @Test
    @DisplayName("채널 정보를 수정하면 실제 DB의 내용이 변경되어야 한다")
    void updateChannel_integration_success() throws Exception {
        // given
        Channel savedChannel = channelRepository.save(new Channel("원래이름", "원래설명", ChannelType.PUBLIC));

        UUID targetId = savedChannel.getId();

        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("바뀐이름", "바뀐설명");

        // when
        mockMvc.perform(patch("/api/channels/{channelId}", targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());


        // then
        Channel updatedChannel = channelRepository.findById(targetId).orElseThrow();
        assertThat(updatedChannel.getDescription()).isEqualTo("바뀐설명");
        assertThat(updatedChannel.getName()).isEqualTo("바뀐이름");
    }

    @Test
    @DisplayName("채널을 삭제하면 실제 DB에서 완전히 사라져야 한다")
    void deleteChannel_integration_success() throws Exception {
        // given
        Channel channel = new Channel();
        channel.setType(ChannelType.PRIVATE);
        Channel savedChannel = channelRepository.save(channel);
        UUID targetId = savedChannel.getId();

        // when
        mockMvc.perform(delete("/api/channels/{channelId}", targetId))
                .andExpect(status().isNoContent());

        // then:
        Optional<Channel> deletedChannel = channelRepository.findById(targetId);
        assertThat(deletedChannel).isEmpty();
    }
}
