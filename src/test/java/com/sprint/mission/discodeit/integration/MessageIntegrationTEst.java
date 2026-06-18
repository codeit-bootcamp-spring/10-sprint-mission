package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MessageIntegrationTEst {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;

    @MockitoBean
    private BinaryContentStorage binaryContentStorage;

    private User author;
    private Channel channel;

    @BeforeEach
    void setUp() {
        author  = userRepository.save(new User("메시지테스터", "msg@test.com", "1234"));
        channel = channelRepository.save(new Channel("메시지채널", "설명", ChannelType.PUBLIC));
    }

    @Test
    @DisplayName("메시지 생성 통합 테스트")
    void createMessage_Success() throws Exception {
        // given
        MessageCreateRequest request = new MessageCreateRequest(author.getId(), channel.getId(), "안녕하세요!");

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        // then
        assertThat(messageRepository.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("메시지 수정 통합 테스트")
    void updateMessage_Success() throws Exception {
        // given
        Message savedMessage = messageRepository.save(new Message(author, "수정 전", channel));
        UUID messageId = savedMessage.getId();

        MessageUpdateRequest updateRequest = new MessageUpdateRequest("수정 완료!");

        // when
        mockMvc.perform(patch("/api/messages/{id}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // then
        Message updated = messageRepository.findById(messageId).get();
        assertThat(updated.getContent()).isEqualTo("수정 완료!");
    }

    @Test
    @DisplayName("메시지 삭제 통합 테스트")
    void deleteMessage_Success() throws Exception {
        // given
        Message savedMessage = messageRepository.save(new Message(author,"삭제될 놈", channel));
        UUID messageId = savedMessage.getId();

        // when
        mockMvc.perform(delete("/api/messages/{id}", messageId))
                .andExpect(status().isNoContent());

        // then
        assertThat(messageRepository.findById(messageId)).isEmpty();
    }
}
