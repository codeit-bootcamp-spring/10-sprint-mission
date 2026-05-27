package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.message.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.entity.*;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
@DataJpaTest(showSql = false)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    Instant now;
    Instant nowMinus5;
    Instant nowMinus10;

    @BeforeEach
    void setUp() {
        readStatusRepository.deleteAll();
        binaryContentRepository.deleteAll();
        messageRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();

        now = Instant.now();
        nowMinus5 = now.minus(5, ChronoUnit.MINUTES);
        nowMinus10 = now.minus(10, ChronoUnit.MINUTES);
    }

    private User createUser(String email, String username, String password, BinaryContent profile) {
        User author = new User(email, username, password, profile);

        return userRepository.save(author);
    }

    private Channel createChannel(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return channelRepository.save(channel);
    }

    private Message createMessage(Channel channel, User author, String content, List<BinaryContent> attachments) {
        Message message = new Message(channel, author, content);

        if (attachments != null && !attachments.isEmpty()) {
            for (BinaryContent attachment : attachments) {
                message.addAttachment(attachment);
            }
        }

        return messageRepository.save(message);
    }

    private BinaryContent createBinaryContent(String fileName, String contentType, Long size) {
        BinaryContent binaryContent = new BinaryContent(fileName, contentType, size);
        return binaryContentRepository.save(binaryContent);
    }

    @Test
    @DisplayName("해당 ID를 가진 메시지를 조회할 수 있다.")
    void find_message_by_id_with_author_channel_attachments() {
        // given(준비)
        User author = createUser("test1@gmail.com", "test1", "1234", null);

        Channel channel = createChannel(ChannelType.PUBLIC, "test1Channel", "test1Channel입니다.");

        List<BinaryContent> attachments = List.of(
                createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length),
                createBinaryContent("test2Binary", "image/png", (long) "test2".getBytes().length)
        );
        Message message = messageRepository.save(createMessage(channel, author, "test1MessageContent", attachments));

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        Message result = messageRepository.findByIdWithAuthorAndChannel(message.getId()).orElseThrow();

        // then(검증)(fetch join) - 지금 이 연관 객체/컬렉션이 이미 초기화되어 있는지 검증
        assertThat(Hibernate.isInitialized(result.getAuthor())).isTrue();
        assertThat(Hibernate.isInitialized(result.getChannel())).isTrue();
        assertThat(Hibernate.isInitialized(result.getAttachments())).isTrue();
    }

    @Test
    @DisplayName("채널별 가장 최신 메시지 생성 시간을 조회할 수 있다.")
    void find_last_message_createdAt_with_channelIds() {
        // given(준비)
        User author1 = createUser("test1@gmail.com", "test1", "1234", null);
        User author2 = createUser("test2@gmail.com", "test2", "1234", null);

        Channel channel1 = createChannel(ChannelType.PUBLIC, "test1Channel", "test1Channel입니다.");
        Channel channel2 = createChannel(ChannelType.PRIVATE, "test2Channel", "test2Channel입니다.");
        Channel channel3 = createChannel(ChannelType.PRIVATE, "test3Channel", "test3Channel입니다.");

        createMessage(channel1, author1, "test1MessageContent", null);
        createMessage(channel1, author2, "test2MessageContent", null);
        createMessage(channel2, author1, "test3MessageContent", null);
        createMessage(channel2, author1, "test4MessageContent", null);

        List<UUID> channelIds = List.of(channel1.getId(), channel2.getId());

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        List<ChannelLastMessageAtDto> result = messageRepository.findLastMessageAtDtoByChannelIds(channelIds);

        // then(검증)
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(dto -> dto.id())
                .containsExactlyInAnyOrder(channel1.getId(), channel2.getId())
                .doesNotContain(channel3.getId());
    }

    @Test
    @DisplayName("특정 채널의 메시지 목록을 페이지네이션으로 조회할 수 있다.")
    void find_All_message_by_channelIds() throws InterruptedException {
        // given(준비)
        User author1 = createUser("test1@gmail.com", "test1", "1234", createBinaryContent("test1Binary", "image/png", (long) "test1".getBytes().length));
        User author2 = createUser("test2@gmail.com", "test2", "1234", null);
        User author3 = createUser("test3@gmail.com", "test3", "1234", null);

        Channel channel1 = createChannel(ChannelType.PUBLIC, "test1Channel", "test1Channel입니다.");
        Channel channel2 = createChannel(ChannelType.PRIVATE, "test2Channel", "test2Channel입니다.");

        List<BinaryContent> attachments = List.of(
                createBinaryContent("test2Binary", "image/png", (long) "test2".getBytes().length),
                createBinaryContent("test3Binary", "image/png", (long) "test3".getBytes().length)
        );
        Message message1 = messageRepository.save(createMessage(channel1, author1, "test1MessageContent", attachments));
        Thread.sleep(10);
        Message message2 = messageRepository.save(createMessage(channel1, author1, "test2MessageContent", null));
        Thread.sleep(10);
        Message message3 = messageRepository.save(createMessage(channel1, author2, "test3MessageContent", null));
        Thread.sleep(10);
        Message message4 = messageRepository.save(createMessage(channel1, author1, "test4MessageContent", null));
        Thread.sleep(10);
        Message message5 = messageRepository.save(createMessage(channel2, author1, "test4MessageContent", null));

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // 나노초가 DB에서 잘리기 때문에 db에서 받아온 message 사용하기
        Message reloadMessage4 = messageRepository.findById(message4.getId()).orElseThrow();
        Instant cursor = reloadMessage4.getCreatedAt();
        Pageable pageable = PageRequest.of(0, 10);

        // when(실행)
        Slice<Message> result = messageRepository.findAllByChannelId(channel1.getId(), cursor, pageable);

        // then(검증)
        assertEquals(3, result.getNumberOfElements());
        assertThat(result)
                .extracting(message -> message.getId())
                .containsExactly(message3.getId(), message2.getId(), message1.getId())
                .doesNotContain(message4.getId(), message5.getId());
        assertThat(result)
                .extracting(message -> message.getChannel().getId())
                .contains(channel1.getId())
                .doesNotContain(channel2.getId());
        assertThat(result)
                .extracting(message -> message.getAuthor().getId())
                .contains(author1.getId(),  author2.getId())
                .doesNotContain(author3.getId());

        // sort 비교
        List<Message> messageList = result.getContent();
        assertThat(messageList.get(0).getCreatedAt()).isAfterOrEqualTo(messageList.get(1).getCreatedAt());
        assertThat(messageList.get(1).getCreatedAt()).isAfterOrEqualTo(messageList.get(2).getCreatedAt());
    }
}