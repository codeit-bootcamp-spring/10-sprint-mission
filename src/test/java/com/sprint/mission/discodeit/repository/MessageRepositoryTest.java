package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.Role;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureMockMvc(addFilters = false)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    private User user;
    private Channel channel;

    @BeforeEach
    void setUp() {
        user = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);
        userRepository.save(user);
        channel = new Channel("공지방", IsPrivate.PUBLIC, "공지방입니다.");
        channelRepository.save(channel);
    }

    @Test
    @DisplayName("userId로 메시지 목록 조회 성공")
    void find_all_by_user_id_success() {
        // given
        Message message1 = new Message(user, channel, "첫 번째 메시지", new ArrayList<>());
        Message message2 = new Message(user, channel, "두 번째 메시지", new ArrayList<>());
        messageRepository.save(message1);
        messageRepository.save(message2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Slice<Message> result = messageRepository.findAllByUserId(user.getId(), pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result).extracting("content")
                .containsExactlyInAnyOrder("첫 번째 메시지", "두 번째 메시지");
    }

    @Test
    @DisplayName("userId로 메시지 목록 조회 실패 - 메시지 없음")
    void find_all_by_user_id_fail_no_messages() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Slice<Message> result = messageRepository.findAllByUserId(user.getId(), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("채널 ID로 가장 최근 메시지 조회 성공")
    void find_first_by_channel_id_order_by_created_at_desc_success() throws InterruptedException {
        // given
        Message message1 = new Message(user, channel, "첫 번째 메시지", new ArrayList<>());
        messageRepository.save(message1);
        Thread.sleep(5);
        Message message2 = new Message(user, channel, "두 번째 메시지", new ArrayList<>());
        messageRepository.save(message2);

        // when
        Optional<Message> result = messageRepository.findFirstByChannelIdOrderByCreatedAtDescIdDesc(
                channel.getId());

        // then
        assertThat(result).isPresent();
        assertEquals("두 번째 메시지", result.get().getContent());
    }

    @Test
    @DisplayName("채널 ID로 가장 최근 메시지 조회 실패 - 메시지 없음")
    void find_first_by_channel_id_order_by_created_at_desc_fail_no_messages() {
        // when
        Optional<Message> result = messageRepository.findFirstByChannelIdOrderByCreatedAtDescIdDesc(
                channel.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 ID로 메시지 페이징 조회 성공")
    void find_all_by_channel_id_success() throws InterruptedException {
        // given
        Message message1 = new Message(user, channel, "첫 번째 메시지", new ArrayList<>());
        messageRepository.save(message1);
        messageRepository.flush();

        Thread.sleep(10);

        Message message2 = new Message(user, channel, "두 번째 메시지", new ArrayList<>());
        messageRepository.save(message2);
        messageRepository.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Message> result = messageRepository.findAllByChannelId(channel.getId(), pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertEquals("두 번째 메시지", result.getContent().get(0).getContent());
    }

    @Test
    @DisplayName("채널 ID로 메시지 페이징 조회 실패 - 메시지 없음")
    void find_all_by_channel_id_fail_noMessages() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Slice<Message> result = messageRepository.findAllByChannelId(channel.getId(), pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("커서 기반 메시지 페이징 조회 성공")
    void find_all_by_channel_id_before_cursor_success() throws InterruptedException {
        // given
        Message message1 = new Message(user, channel, "첫 번째 메시지", new ArrayList<>());
        messageRepository.saveAndFlush(message1);

        Thread.sleep(500);

        Message message2 = new Message(user, channel, "두 번째 메시지", new ArrayList<>());
        messageRepository.saveAndFlush(message2);

        Instant cursor = messageRepository.findById(message2.getId()).get().getCreatedAt();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdBeforeCursor(
                channel.getId(), cursor, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertEquals("첫 번째 메시지", result.getContent().get(0).getContent());
    }

    @Test
    @DisplayName("커서 기반 메시지 페이징 조회 실패 - 커서 이전 메시지 없음")
    void find_all_by_channel_id_before_cursor_fail_noMessages() {
        // given
        Instant cursor = Instant.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdBeforeCursor(
                channel.getId(), cursor, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("채널별 마지막 메시지 조회 성공")
    void find_last_messages_by_channel_ids_success() throws InterruptedException {
        // given
        Message message1 = new Message(user, channel, "첫 번째 메시지", new ArrayList<>());
        messageRepository.save(message1);
        Thread.sleep(50);

        Message message2 = new Message(user, channel, "두 번째 메시지", new ArrayList<>());
        messageRepository.save(message2);

        // when
        List<Message> result = messageRepository.findLastMessagesByChannelIds(
                List.of(channel.getId()));

        // then
        assertThat(result).hasSize(1);
        assertEquals("두 번째 메시지", result.get(0).getContent());
    }

    @Test
    @DisplayName("채널별 마지막 메시지 조회 실패 - 메시지 없음")
    void find_last_messages_by_channel_ids_fail_noMessages() {
        // when
        List<Message> result = messageRepository.findLastMessagesByChannelIds(
                List.of(channel.getId()));

        // then
        assertThat(result).isEmpty();
    }
}
