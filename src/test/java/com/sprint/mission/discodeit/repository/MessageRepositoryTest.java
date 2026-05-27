package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    TestEntityManager em;

    private User author;
    private Channel channel;
    private UUID messageId;
    @BeforeEach
    void setUp() {
        author = new User("작성자", "author@test.com", "12345678", null);
        new UserStatus(author);
        em.persist(author);

        channel = new Channel("테스트 채널", "테스트 채널입니다.");
        em.persist(channel);

        Message msg = new Message("첫 번째", channel, author, List.of());

        em.persist(msg);
        em.persist(new Message("두 번째", channel, author, List.of()));
        em.persist(new Message("세 번째", channel, author, List.of()));
        em.flush();
        messageId = msg.getId();
        em.clear();
    }

    @Test
    @DisplayName("커서 이후 메시지 페이징 조회 성공")
    void findAllByChannelIdWithAuthor_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channel.getId(), channel.getCreatedAt().plusSeconds(10), pageable);

        // then
        assertEquals(3, result.getContent().size());
    }

    @Test
    @DisplayName("커서 과거이면 메시지 페이징 조회 실패")
    void findAllByChannelIdWithAuthor_실패() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // when
        // repository는 예외를 반환하지 않고 Optional.empty()를 반환한다!!
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channel.getId(), channel.getCreatedAt().minusSeconds(10), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasContent()).isFalse();
    }

//  보류
//    @Test
//    @DisplayName("채널의 최신 메시지 시간 반환")
//    void findFirstByChannelIdOrderByCreatedAtDesc_성공() {
//        // given
//        Instant recentCreatedAt = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel.getId());
//        Message msg = messageRepository.findById(messageId).get();
//        System.out.println(msg.getCreatedAt());
//        System.out.println(recentCreatedAt);
//        // when, then
//        assertEquals(msg.getCreatedAt(), recentCreatedAt);
//    }
//
//    @Test
//    @DisplayName("채널의 최신 메시지 시간 반환")
//    void findFirstByChannelIdOrderByCreatedAtDesc_실패() {
//    }
}