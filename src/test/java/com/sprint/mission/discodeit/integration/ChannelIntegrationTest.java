package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class ChannelIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ChannelRepository channelRepository;
    @Autowired private ChannelService channelService;
    @Autowired private UserService userService;
    @Autowired private EntityManager em;

    @Test
    @DisplayName("public 채널 생성 통합 테스트")
    void create_public_channel_success() throws Exception {
        // given
        CreatePublicChannelRequestDto request = new CreatePublicChannelRequestDto("Integration Public", "Public Desc");

        // when
        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Integration Public"));

        // then
        List<Channel> channels = channelRepository.findAll();
        assertThat(channels).hasSize(1);
        assertThat(channels.get(0).getName()).isEqualTo("Integration Public");
        assertThat(channels.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
    }

    @Test
    @DisplayName("private 채널 생성 통합 테스트")
    void create_private_channel_success() throws Exception {
        // given
        UserDto userDto = userService.create(new CreateUserRequestDto("privUser", "priv@test.com", "pass1234", null));
        em.flush();
        em.clear();

        CreatePrivateChannelRequestDto request = new CreatePrivateChannelRequestDto(List.of(userDto.id()));

        // when
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("PRIVATE"));

        // then
        List<Channel> channels = channelRepository.findAll();
        assertThat(channels).hasSize(1);
        assertThat(channels.get(0).getType()).isEqualTo(ChannelType.PRIVATE);
    }

    @Test
    @DisplayName("채널 정보 수정 통합 테스트")
    void update_channel_success() throws Exception {
        // given
        ChannelDto savedChannel = channelService.createPublic(new CreatePublicChannelRequestDto("Old Name", "Old Desc"));
        em.flush();
        em.clear();

        UpdateChannelRequestDto request = new UpdateChannelRequestDto("New Name", "New Desc");

        // when
        mockMvc.perform(patch("/api/channels/{channelId}", savedChannel.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        // then
        em.flush();
        em.clear(); // 업데이트 된 최신 상태를 DB에서 꺼내오기 위해 캐시 비우기
        Channel updatedChannel = channelRepository.findById(savedChannel.id()).orElseThrow();
        assertThat(updatedChannel.getName()).isEqualTo("New Name");
        assertThat(updatedChannel.getDescription()).isEqualTo("New Desc");
    }

    @Test
    @DisplayName("채널 삭제 통합 테스트")
    void delete_channel_success() throws Exception {
        // given
        ChannelDto savedChannel = channelService.createPublic(new CreatePublicChannelRequestDto("To Be Deleted", "Desc"));
        em.flush();
        em.clear();

        // when
        mockMvc.perform(delete("/api/channels/{channelId}", savedChannel.id()))
            .andExpect(status().isNoContent());

        // then
        boolean exists = channelRepository.existsById(savedChannel.id());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("프라이빗 채널 생성 시 참여자가 없으면 400 Bad Request가 발생한다.")
    void create_private_channel_empty_participants_exception() throws Exception {
        // given: 빈 참여자 리스트 (DTO Validation 에러 유도)
        CreatePrivateChannelRequestDto request = new CreatePrivateChannelRequestDto(List.of());

        // when & then
        mockMvc.perform(post("/api/channels/private")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest()); // 400 에러 검증
    }

    @Test
    @DisplayName("프라이빗 채널 수정 시 400 Bad Request가 발생한다.")
    void update_private_channel_exception() throws Exception {
        // given: 유저와 프라이빗 채널을 먼저 생성
        UserDto userDto = userService.create(new CreateUserRequestDto("privUpdater", "privUp@test.com", "pass1234", null));
        ChannelDto privateChannel = channelService.createPrivate(new CreatePrivateChannelRequestDto(List.of(userDto.id())));
        em.flush();
        em.clear();

        UpdateChannelRequestDto request = new UpdateChannelRequestDto("New Name", "New Desc");

        // when & then: 프라이빗 채널에 수정(PATCH) 시도 시 비즈니스 예외 발생 확인
        mockMvc.perform(patch("/api/channels/{channelId}", privateChannel.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest()); // 400 에러 검증
    }
}