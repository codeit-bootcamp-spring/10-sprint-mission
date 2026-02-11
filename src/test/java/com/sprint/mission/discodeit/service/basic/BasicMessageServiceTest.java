package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BasicMessageServiceTest {

    @Autowired
    BasicMessageService messageService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    BinaryContentRepository binaryContentRepository;

    UUID userId;
    UUID channelId;

    @BeforeEach
    void setUp() {
        FileIOHelper.flushData();

        User user = new User(
                "testUser",
                "1234",
                "test@test.com"
        );
        userRepository.save(user);
        userId = user.getId();

        Channel channel = Channel.buildPublic(
                "test-channel",
                "test-desc"
        );
        channelRepository.save(channel);
        channelId = channel.getId();
    }

    @ParameterizedTest
    @MethodSource("createMessageProvider")
    @DisplayName("메시지 생성 성공")
    void createMessage_success(List<BinaryContentRequest> attachments) {

        // given
        CreateMessageRequest request =
                new CreateMessageRequest(
                        channelId,
                        "hello world",
                        attachments
                );

        // when
        UUID messageId = messageService.createMessage(userId, request);

        // then
        Message message = messageRepository.findById(messageId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        Channel channel = channelRepository.findById(channelId).orElseThrow();

        assertThat(message.getContent()).isEqualTo("hello world");
        assertThat(message.getSenderId()).isEqualTo(userId);
        assertThat(message.getChannelId()).isEqualTo(channelId);

        assertThat(user.getMessageIds()).contains(messageId);
        assertThat(channel.getMessageIds()).contains(messageId);

        if (attachments == null || attachments.isEmpty()) {
            assertThat(message.getAttachmentIds()).isEmpty();
        } else {
            assertThat(message.getAttachmentIds()).hasSize(1);

            UUID attachmentId = message.getAttachmentIds().get(0);
            BinaryContent binaryContent =
                    binaryContentRepository.findById(attachmentId).orElseThrow();

            assertThat(binaryContent.getOwnerId()).isEqualTo(userId);
            assertThat(binaryContent.getBinaryContentOwnerType())
                    .isEqualTo(BinaryContentOwnerType.MESSAGE);
        }
    }

    static Stream<Arguments> createMessageProvider() {
        return Stream.of(
                Arguments.of((List<BinaryContentRequest>) null),
                Arguments.of(List.of()),
                Arguments.of(List.of(
                        new BinaryContentRequest(
                                BinaryContentOwnerType.MESSAGE,
                                new MockMultipartFile(
                                        "file",
                                        "image.png",
                                        "image/png",
                                        "image".getBytes()
                                )
                        )
                ))
        );
    }


    @Test
    @DisplayName("채널별 메시지 조회")
    void findMessagesByChannel() {

        // given
        UUID messageId = messageService.createMessage(
                userId,
                new CreateMessageRequest(
                        channelId,
                        "hello",
                        List.of()
                )
        );

        // when
        var responses =
                messageService.findAllMessagesByChannelId(channelId);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).content()).isEqualTo("hello");
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage_success() {

        // given
        UUID messageId = messageService.createMessage(
                userId,
                new CreateMessageRequest(
                        channelId,
                        "old",
                        List.of()
                )
        );

        UpdateMessageRequest request =
                new UpdateMessageRequest(
                        messageId,
                        "new content",
                        List.of()
                );

        // when
        var response =
                messageService.updateMessage(userId, request);

        // then
        Message message =
                messageRepository.findById(messageId).orElseThrow();

        assertThat(message.getContent()).isEqualTo("new content");
        assertThat(response.content()).isEqualTo("new content");
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage_success() {

        // given
        UUID messageId = messageService.createMessage(
                userId,
                new CreateMessageRequest(
                        channelId,
                        "delete me",
                        List.of(
                                new BinaryContentRequest(
                                        BinaryContentOwnerType.MESSAGE,
                                        new MockMultipartFile(
                                                "file",
                                                "img.png",
                                                "image/png",
                                                "img".getBytes()
                                        )
                                )
                        )
                )
        );

        Message message =
                messageRepository.findById(messageId).orElseThrow();

        List<UUID> attachmentIds = message.getAttachmentIds();

        // when
        messageService.deleteMessage(userId, messageId);

        // then
        assertThat(messageRepository.findById(messageId)).isEmpty();

        User user = userRepository.findById(userId).orElseThrow();
        Channel channel = channelRepository.findById(channelId).orElseThrow();

        assertThat(user.getMessageIds()).doesNotContain(messageId);
        assertThat(channel.getMessageIds()).doesNotContain(messageId);

        for (UUID attachmentId : attachmentIds) {
            assertThat(binaryContentRepository.findById(attachmentId)).isEmpty();
        }
    }
}
