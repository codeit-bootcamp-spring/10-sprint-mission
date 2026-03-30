package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.TestJpaAuditConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestJpaAuditConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("메시지가 비어있지 않으면 정상 등록됩니다.")
    void save_message() {
        // given
        User author = new User("author", "test@test.com", "1235", null);
        Channel channel = new Channel("test", "desc", ChannelType.PUBLIC);
        Message message = new Message(author, channel, "content", List.of());

        // when
        Message savedMessage = messageRepository.save(message);

        // then
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getAuthor()).isEqualTo(author);
        assertThat(savedMessage.getChannel()).isEqualTo(channel);
    }

    @Test
    @DisplayName("cursor 이전 채널 메시지를 조회합니다.")
    void find_by_channel_id_with_cursor() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1",
                Instant.parse("2026-03-30T10:00:00Z"));
        createMessage(user, channel, "m2",
                Instant.parse("2026-03-30T09:00:00Z"));
        createMessage(user, channel, "m3",
                Instant.parse("2026-03-30T08:00:00Z"));

        Instant cursor = Instant.parse("2026-03-30T09:30:00Z");
        Pageable pageable = Pageable.ofSize(10);

        // when
        Slice<Message> result =
                messageRepository.findByChannelIdWithCursor(channel.getId(), cursor, pageable);

        // then
        assertThat(result.getContent())
                .extracting(Message::getContent)
                .containsExactly("m2", "m3");
    }

    @Test
    @DisplayName("cursor 이전 메시지가 없으면 빈 결과를 반환합니다.")
    void find_by_channel_id_with_cursor_empty() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1",
                Instant.parse("2026-03-30T10:00:00Z"));

        Instant cursor = Instant.parse("2026-03-30T07:00:00Z");
        Pageable pageable = Pageable.ofSize(10);

        // when
        Slice<Message> result =
                messageRepository.findByChannelIdWithCursor(channel.getId(), cursor, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("채널 메시지를 최신순으로 조회합니다.")
    void find_by_channel_id_order_by_created_at_desc() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1",
                Instant.parse("2026-03-30T08:00:00Z"));
        createMessage(user, channel, "m2",
                Instant.parse("2026-03-30T09:00:00Z"));

        Pageable pageable = Pageable.ofSize(10);

        // when
        Slice<Message> result =
                messageRepository.findByChannel_IdOrderByCreatedAtDesc(channel.getId(), pageable);

        // then
        assertThat(result.getContent())
                .extracting(Message::getContent)
                .containsExactly("m2", "m1");
    }

    @Test
    @DisplayName("작성자 기준 메시지 최신순 조회")
    void find_by_author_order_desc() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1",
                Instant.parse("2026-03-30T08:00:00Z"));
        createMessage(user, channel, "m2",
                Instant.parse("2026-03-30T09:00:00Z"));

        Pageable pageable = Pageable.ofSize(10);

        // when
        Slice<Message> result =
                messageRepository.findByAuthor_IdOrderByCreatedAtDesc(user.getId(), pageable);

        // then
        assertThat(result.getContent())
                .extracting(Message::getContent)
                .containsExactly("m2", "m1");
    }

    @Test
    @DisplayName("작성자 cursor 기반 조회")
    void find_by_author_with_cursor() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1",
                Instant.parse("2026-03-30T10:00:00Z"));
        createMessage(user, channel, "m2",
                Instant.parse("2026-03-30T09:00:00Z"));

        Instant cursor = Instant.parse("2026-03-30T09:30:00Z");
        Pageable pageable = Pageable.ofSize(10);

        // when
        Slice<Message> result =
                messageRepository.findByAuthorIdWithCursor(user.getId(), cursor, pageable);

        // then
        assertThat(result.getContent())
                .extracting(Message::getContent)
                .containsExactly("m2");
    }

    @Test
    @DisplayName("채널의 모든 메시지 조회")
    void find_all_by_channel_id() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1", Instant.now());
        createMessage(user, channel, "m2", Instant.now());

        // when
        List<Message> result = messageRepository.findAllByChannel_Id(channel.getId());

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("채널의 최신 메시지 1개 조회")
    void find_top_by_channel() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1",
                Instant.parse("2026-03-30T08:00:00Z"));
        createMessage(user, channel, "m2",
                Instant.parse("2026-03-30T09:00:00Z"));

        // when
        Optional<Message> result =
                messageRepository.findTopByChannel_IdOrderByCreatedAtDesc(channel.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo("m2");
    }

    @Test
    @DisplayName("채널별 마지막 메시지 시간 조회")
    void find_last_message_at_by_channel_ids() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1",
                Instant.parse("2026-03-30T08:00:00Z"));
        createMessage(user, channel, "m2",
                Instant.parse("2026-03-30T09:00:00Z"));

        // when
        List<Object[]> result =
                messageRepository.findLastMessageAtByChannelIds(List.of(channel.getId()));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)[0]).isEqualTo(channel.getId());
        assertThat(result.get(0)[1]).isEqualTo(Instant.parse("2026-03-30T09:00:00Z"));
    }

    @Test
    @DisplayName("채널의 메시지를 모두 삭제합니다.")
    void delete_all_by_channel_id() {
        // given
        User user = createUser();
        Channel channel = createChannel();

        createMessage(user, channel, "m1", Instant.now());
        createMessage(user, channel, "m2", Instant.now());

        // when
        messageRepository.deleteAllByChannel_Id(channel.getId());

        // then
        List<Message> result = messageRepository.findAllByChannel_Id(channel.getId());
        assertThat(result).isEmpty();
    }

    private User createUser() {
        User user = new User("user", "test@test.com", "1234", null);
        return userRepository.saveAndFlush(user);
    }

    private Channel createChannel() {
        Channel channel = new Channel("channel", "desc", ChannelType.PUBLIC);
        return channelRepository.saveAndFlush(channel);
    }

    private Message createMessage(User user, Channel channel, String content, Instant createdAt) {
        Message message = new Message(user, channel, content, List.of());
        Message saved = messageRepository.saveAndFlush(message);

        em.getEntityManager()
                .createQuery("update Message m set m.createdAt = :createdAt where m.id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("id", saved.getId())
                .executeUpdate();

        em.clear();

        return messageRepository.findById(saved.getId()).orElseThrow();
    }
}