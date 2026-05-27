package com.sprint.mission.discodeit.integration;

import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import java.util.UUID;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
class MessageIntegrationTest extends IntegrationTest {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;

    // 사용자 생성 헬퍼
    private UUID createUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest("달선", "dalsun@naver.com", "password123");
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request));

        String response = mockMvc.perform(multipart("/api/users").file(requestPart))
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(JsonPath.read(response, "$.id"));
    }

    // 채널 생성 헬퍼
    private UUID createChannel() throws Exception {
        UUID adminId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        setSecurityContext(adminId, Role.CHANNEL_MANAGER);

        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지방", "공지방입니다.");
        String response = mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(JsonPath.read(response, "$.id"));
    }

    // 메시지 생성 헬퍼
    private UUID createMessage(UUID authorId, UUID channelId, String content) throws Exception {
        MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, content);
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request));

        String response = mockMvc.perform(multipart("/api/messages").file(requestPart))
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(JsonPath.read(response, "$.id"));
    }

    @Test
    @DisplayName("메시지 생성 성공 - 모든 필수 값 포함")
    void create_message_success() throws Exception {
        // given
        UUID authorId = createUser();
        UUID channelId = createChannel();
        MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "반갑습니다!");
        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request));

        // when & then
        mockMvc.perform(multipart("/api/messages").file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("반갑습니다!"));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 존재하지 않는 채널")
    void createMessage_fail_channel_not_found() throws Exception {
        // given
        User author = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);
        User savedAuthor = userRepository.save(author);

        MessageCreateRequest request = new MessageCreateRequest(savedAuthor.getId(),
                UUID.randomUUID(),
                "안녕");

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest", // 컨트롤러의 파라미터 이름과 일치해야 함
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    @Test
    @DisplayName("메시지 수정 성공 - 유효한 내용으로 변경")
    void update_message_success() throws Exception {
        // given
        UUID authorId = createUser();
        UUID channelId = createChannel();
        setSecurityContext(authorId, Role.USER);
        UUID messageId = createMessage(authorId, channelId, "원본 내용");
        MessageUpdateRequest request = new MessageUpdateRequest("수정된 내용입니다.");

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 내용입니다."));
    }

    @Test
    @DisplayName("메시지 수정 실패 - 내용이 공백인 경우")
    void update_message_fail_blank_content() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("   ");

        // when, then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메시지 삭제 성공 - 정상적인 ID 요청")
    void delete_message_success() throws Exception {
        // given
        UUID authorId = createUser();
        UUID channelId = createChannel();
        setSecurityContext(authorId, Role.USER);
        UUID messageId = createMessage(authorId, channelId, "삭제할 메시지");

        // when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("메시지 삭제 실패 - 존재하지 않는 메시지 ID")
    void deleteMessage_Fail_NotFound() throws Exception {
        // given
        UUID invalidMessageId = UUID.randomUUID();
        setSecurityContext(UUID.randomUUID(), Role.USER);

        // when, then
        mockMvc.perform(delete("/api/messages/{messageId}", invalidMessageId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));
    }

    @Test
    @DisplayName("메시지 목록 조회 성공 - 채널 ID 파라미터 포함")
    void find_all_messages_success() throws Exception {
        // given
        Channel channel = new Channel("공지방", IsPrivate.PUBLIC, "공지방입니다.");

        Channel savedChannel = channelRepository.save(channel);

        // when, then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", savedChannel.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("메시지 목록 조회 실패 - 존재하지 않는 채널")
    void getMessages_fail_channel_not_found() throws Exception {
        // given
        UUID notExistChannelId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", notExistChannelId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CHANNEL_NOT_FOUND"));
    }

    private void setSecurityContext(UUID userId, Role role) {
        UserDto userDto = new UserDto(userId, "달선", "dalsun@naver.com", null, false, role);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "ekftjs123");
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
