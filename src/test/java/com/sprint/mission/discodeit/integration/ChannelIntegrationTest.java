package com.sprint.mission.discodeit.integration;

import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "CHANNEL_MANAGER")
@AutoConfigureMockMvc(addFilters = false)
class ChannelIntegrationTest extends IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("공개 채널 생성 성공 - 정상 데이터 입력")
    void create_public_channel_success() throws Exception {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("자유게시판",
                "자유롭게 대화하는 채널입니다.");

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("자유게시판"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("공개 채널 생성 실패 - 채널 이름 누락")
    void create_public_channel_fail_empty_name() throws Exception {
        // 이름이 빈 값인 경우 @Valid에 의해 400 Bad Request 발생 가정
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "설명만 있음");

        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비공개 채널 생성 성공 - 참여자 목록 포함")
    void create_private_channel_success() throws Exception {
        User user = new User("달선", "dalsun@naver.com", "password123", null, Role.USER);
        User savedUser = userRepository.save(user);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                List.of(savedUser.getId()));

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("비공개 채널 생성 실패 - 참여자 목록이 비어있음")
    void create_private_channel_fail_empty_participants() throws Exception {
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of());

        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널 수정 성공 - 이름 및 설명 변경")
    void update_channel_success() throws Exception {
        // 먼저 채널 생성
        PublicChannelCreateRequest createRequest = new PublicChannelCreateRequest("공지방", "공지방입니다.");
        String response = mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        UUID channelId = UUID.fromString(JsonPath.read(response, "$.id"));

        // 수정 요청
        PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("수정된 이름",
                "수정된 설명");
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("수정된 이름"));
    }

    @Test
    @DisplayName("채널 수정 실패 - 존재하지 않는 채널 ID")
    void update_channel_fail_not_found() throws Exception {
        UUID invalidId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("변경", "변경");

        mockMvc.perform(patch("/api/channels/{channelId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    @Test
    @DisplayName("채널 삭제 실패 - 이미 존재하지 않는 채널 삭제 시도")
    void delete_channel_fail_not_found() throws Exception {
        UUID invalidId = UUID.randomUUID();

        mockMvc.perform(delete("/api/channels/{channelId}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    @Test
    @DisplayName("채널 삭제 실패 - 존재하지 않는 채널 ID")
    void deleteChannel_fail_notFound() throws Exception {
        // given
        UUID notExistChannelId = UUID.randomUUID();

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", notExistChannelId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

}
