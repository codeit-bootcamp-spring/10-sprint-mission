package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
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

    @PersistenceContext
    private EntityManager entityManager;

    UUID userId;
    UUID channelId;

    @BeforeEach
    void setUp() {

        User user = new User("testUser", "1234", "test@test.com");
        userRepository.save(user);
        userId = user.getId();

        Channel channel = Channel.buildPublic("test-channel", "test-desc");
        channelRepository.save(channel);
        channelId = channel.getId();

        flushAndClear();
    }

    @ParameterizedTest
    @MethodSource("createMessageProvider")
    @DisplayName("메시지 생성 성공")
    void createMessage_success(List<BinaryContentRequest> attachments) {
        MessageCreateRequest request = new MessageCreateRequest("hello world", channelId, userId);

        UUID messageId = messageService.createMessage(userId, request, attachments).id();
        flushAndClear();

        Message message = messageRepository.findById(messageId).orElseThrow();

        assertThat(message.getContent()).isEqualTo("hello world");
        assertThat(message.getAuthor().getId()).isEqualTo(userId);
        assertThat(message.getChannel().getId()).isEqualTo(channelId);

        if (attachments == null || attachments.isEmpty()) {
            assertThat(message.getAttachments()).isEmpty();
        } else {
            assertThat(message.getAttachments()).hasSize(1);

            UUID attachmentId = message.getAttachments().get(0).getId();
            BinaryContent binaryContent = binaryContentRepository.findById(attachmentId).orElseThrow();

            assertThat(binaryContent.getFileName()).isEqualTo("image.png");
            assertThat(binaryContent.getContentType()).isEqualTo("image/png");
        }
    }

    static Stream<Arguments> createMessageProvider() {
        return Stream.of(
                Arguments.of((List<BinaryContentRequest>) null),
                Arguments.of(List.of()),
                Arguments.of(List.of(
                        new BinaryContentRequest(
                                BinaryContentOwnerType.MESSAGE,
                                new MockMultipartFile("file", "image.png", "image/png", "image".getBytes())
                        )
                ))
        );
    }

    @Test
    @DisplayName("채널별 메시지 조회")
    void findMessagesByChannel() {
        messageService.createMessage(
                userId,
                new MessageCreateRequest("hello", channelId, userId),
                List.of()
        );

        PageResponse<MessageDto> responses = messageService.findAllMessagesByChannelId(
                channelId,
                null,
                PageRequest.of(0, 50)
        );

        assertThat(responses.content()).hasSize(1);
        assertThat(responses.content().get(0).content()).isEqualTo("hello");
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage_success() {
        UUID messageId = messageService.createMessage(
                userId,
                new MessageCreateRequest("old", channelId, userId),
                List.of()
        ).id();

        MessageUpdateRequest request = new MessageUpdateRequest("new content");

        MessageDto response = messageService.updateMessage(messageId, request);
        flushAndClear();

        Message message = messageRepository.findById(messageId).orElseThrow();
        assertThat(message.getContent()).isEqualTo("new content");
        assertThat(response.content()).isEqualTo("new content");
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage_success() {
        UUID messageId = messageService.createMessage(
                userId,
                new MessageCreateRequest("delete me", channelId, userId),
                List.of(
                        new BinaryContentRequest(
                                BinaryContentOwnerType.MESSAGE,
                                new MockMultipartFile("file", "img.png", "image/png", "img".getBytes())
                        )
                )
        ).id();

        Message message = messageRepository.findById(messageId).orElseThrow();
        List<UUID> attachmentIds = message.getAttachments().stream()
                .map(BinaryContent::getId)
                .toList();

        messageService.deleteMessage(messageId);
        flushAndClear();

        assertThat(messageRepository.findById(messageId)).isEmpty();

        for (UUID attachmentId : attachmentIds) {
            assertThat(binaryContentRepository.findById(attachmentId)).isPresent();
        }
    }

    @Test
    @DisplayName("Message 생성 시 User, Channel 양방향 연관관계 일관성 유지")
    void message_bidirectionalConsistency() {
        User user = userRepository.findById(userId).orElseThrow();
        Channel channel = channelRepository.findById(channelId).orElseThrow();

        Message message = new Message(user, channel, "bidirectional");

        assertThat(message.getAuthor()).isEqualTo(user);
        assertThat(message.getChannel()).isEqualTo(channel);
        assertThat(user.getMessages()).contains(message);
        assertThat(channel.getMessages()).contains(message);
    }

    @Test
    @DisplayName("메시지 커서 페이지네이션 조회 성공")
    void findMessagesByChannel_withCursor() {
        for (int i = 1; i <= 130; i++) {
            messageService.createMessage(
                    userId,
                    new MessageCreateRequest("m" + i, channelId, userId),
                    List.of()
            );
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        PageResponse<MessageDto> firstPage = messageService.findAllMessagesByChannelId(
                channelId,
                null,
                PageRequest.of(0, 50)
        );

        assertThat(firstPage.content()).hasSize(50);
        assertThat(firstPage.hasNext()).isTrue();
        assertThat(firstPage.nextCursor()).isInstanceOf(UUID.class);

        PageResponse<MessageDto> secondPage = messageService.findAllMessagesByChannelId(
                channelId,
                (UUID) firstPage.nextCursor(),
                PageRequest.of(0, 50)
        );

        assertThat(secondPage.content()).hasSize(50);
        assertThat(secondPage.hasNext()).isTrue();
        assertThat(secondPage.nextCursor()).isInstanceOf(UUID.class);

        PageResponse<MessageDto> thirdPage = messageService.findAllMessagesByChannelId(
                channelId,
                (UUID) secondPage.nextCursor(),
                PageRequest.of(0, 50)
        );

        assertThat(thirdPage.content()).hasSize(30);
        assertThat(thirdPage.hasNext()).isFalse();
        assertThat(thirdPage.nextCursor()).isNull();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
