package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("메시지 채널별 정렬 조회 성공")
    void findByChannelIdOrderByCreatedAtDescIdDesc_success() {
        User user = persistUser("author");
        Channel channel = persistChannel("general");
        Message oldest = persistMessage(user, channel, "oldest", Instant.parse("2026-04-01T00:00:00Z"));
        Message middle = persistMessage(user, channel, "middle", Instant.parse("2026-04-01T00:01:00Z"));
        Message newest = persistMessage(user, channel, "newest", Instant.parse("2026-04-01T00:02:00Z"));
        flushAndClear();

        Slice<Message> slice = messageRepository.findByChannel_IdOrderByCreatedAtDescIdDesc(
                channel.getId(),
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")))
        );

        assertThat(slice.getContent()).extracting(Message::getId)
                .containsExactly(newest.getId(), middle.getId());
        assertThat(slice.hasNext()).isTrue();
        assertThat(slice.getContent()).extracting(Message::getId).doesNotContain(oldest.getId());
    }

    @Test
    @DisplayName("존재하지 않는 채널의 메시지 조회 실패")
    void findByChannelIdOrderByCreatedAtDescIdDesc_fail_notFound() {
        Slice<Message> slice = messageRepository.findByChannel_IdOrderByCreatedAtDescIdDesc(
                UUID.randomUUID(),
                PageRequest.of(0, 10)
        );

        assertThat(slice.getContent()).isEmpty();
    }

    @Test
    @DisplayName("메시지 커서 페이지네이션 조회 성공")
    void findNextPageByChannelIdAndCursor_success() {
        User user = persistUser("author");
        Channel channel = persistChannel("general");
        Message oldest = persistMessage(user, channel, "oldest", Instant.parse("2026-04-01T00:00:00Z"));
        Message middle = persistMessage(user, channel, "middle", Instant.parse("2026-04-01T00:01:00Z"));
        persistMessage(user, channel, "newest", Instant.parse("2026-04-01T00:02:00Z"));
        flushAndClear();

        Slice<Message> slice = messageRepository.findNextPageByChannelIdAndCursor(
                channel.getId(),
                middle.getCreatedAt(),
                middle.getId(),
                PageRequest.of(0, 10)
        );

        assertThat(slice.getContent()).extracting(Message::getId)
                .containsExactly(oldest.getId());
    }

    @Test
    @DisplayName("커서 이후 메시지가 없으면 빈 페이지 반환")
    void findNextPageByChannelIdAndCursor_fail_whenNoNextPage() {
        User user = persistUser("author");
        Channel channel = persistChannel("general");
        Message oldest = persistMessage(user, channel, "oldest", Instant.parse("2026-04-01T00:00:00Z"));
        flushAndClear();

        Slice<Message> slice = messageRepository.findNextPageByChannelIdAndCursor(
                channel.getId(),
                oldest.getCreatedAt(),
                oldest.getId(),
                PageRequest.of(0, 10)
        );

        assertThat(slice.getContent()).isEmpty();
    }

    private User persistUser(String username) {
        User user = new User(username, "1234", username + "@test.com");
        entityManager.persist(user);
        return user;
    }

    private Channel persistChannel(String name) {
        Channel channel = Channel.buildPublic(name, "desc");
        entityManager.persist(channel);
        return channel;
    }

    private Message persistMessage(User user, Channel channel, String content, Instant createdAt) {
        Message message = new Message(user, channel, content);
        entityManager.persist(message);
        entityManager.flush();
        ReflectionTestUtils.setField(message, "createdAt", createdAt);
        entityManager.createNativeQuery("update messages set created_at = ? where id = ?")
                .setParameter(1, createdAt)
                .setParameter(2, message.getId())
                .executeUpdate();
        return message;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
