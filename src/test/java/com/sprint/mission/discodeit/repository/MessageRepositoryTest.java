package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(AppConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;


    /*
        조회
     */
    // [성공]
    @Test
    @DisplayName("단건 조회 완료")
    void find_with_details_success() {
        // given

        // 사용자
        UserEntity author = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(author);

        // 채널
        ChannelEntity channel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);

        // 메시지
        MessageEntity message = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );
        messageRepository.save(message);

        // when
        Optional<MessageEntity> result = messageRepository.findWithDetails(message.getId());

        // then
        assertTrue(result.isPresent());
        assertEquals(author.getUsername(), result.get().getAuthor().getUsername());
        assertEquals(channel.getName(), result.get().getChannel().getName());
        assertEquals(message.getContent(), result.get().getContent());
        assertNotNull(result.get().getCreatedAt());
    }

    // [살퍄]
    @Test
    @DisplayName("다건 조회 실패: 메시지가 존재하지 않을 경우, 빈 리스트 반환")
    void find_all_with_details_failure() {
        // given

        // when
        List<MessageEntity> result = messageRepository.findAllWithDetails();

        // then
        assertTrue(result.isEmpty());
    }

    /*
        단건 조회
        -------------
        특정 채널에서 마지막으로 발행된 메시지 시간 조회
     */
    // [성공]
    @Test
    @DisplayName("특정 채널에서 마지막으로 발행된 메시지 시간 조회 완료")
    void get_last_message_at_success() {
        // given

        // 사용자
        UserEntity author = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(author);

        // 채널
        ChannelEntity channel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);

        // 메시지
        MessageEntity firstMessage = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );
        MessageEntity secondMessage = new MessageEntity(
                "KILL ME KILL ME",
                author,
                channel
        );
        List<MessageEntity> messages = List.of(firstMessage, secondMessage);
        messageRepository.saveAll(messages);

        // when
        Instant result = messageRepository.getLastMessageAt(channel.getId());

        // then
        assertNotNull(result);
        assertEquals(secondMessage.getCreatedAt(), result);
        assertTrue(result.isAfter(firstMessage.getCreatedAt())
                || result.equals(secondMessage.getCreatedAt())
        );
    }

    // [실패] 메시지 미존재
    @Test
    @DisplayName("특정 챈러에서 마지막으로 발행한 메시지 시간 조히 실패: 메시지가 존재하지 않을 경우, null 반환")
    void get_last_message_at_failure() {
        // given
        UUID channelId = UUID.randomUUID();

        // when
        Instant result = messageRepository.getLastMessageAt(channelId);

        // then
        assertNull(result);
    }

    /*
        다건 조회
     */
    // [성공]
    @Test
    @DisplayName("사용자를 통한 다건 조회 완료")
    void find_all_with_details_by_author_success() {
        // given

        // 사용자
        UserEntity author = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(author);

        // 채널
        ChannelEntity channel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);

        // 메시지
        MessageEntity firstMessage = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );
        MessageEntity secondMessage = new MessageEntity(
                "KILL ME KILL ME",
                author,
                channel
        );
        List<MessageEntity> messages = List.of(firstMessage, secondMessage);
        messageRepository.saveAll(messages);

        // when
        List<MessageEntity> result = messageRepository.findByAuthor(author);

        // then
        assertEquals(2, result.size());
    }

    // [실패] 사용자 미존재
    @Test
    @DisplayName("사용자를 통한 다건 조회 실패: 메시지가 존재하지 않을 경우, 빈 리스트 반환")
    void find_all_with_details_by_author_failure() {
        // given

        // 사용자
        UserEntity author = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(author);

        // when
        List<MessageEntity> result = messageRepository.findByAuthor(author);

        // then
        assertTrue(result.isEmpty());
    }

    // [성공]
    @Test
    @DisplayName("채널을 통한 다건 조회 완료")
    void find_all_with_details_by_channel_success() {
        // given

        // 사용자
        UserEntity author = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(author);

        // 채널
        ChannelEntity channel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);

        // 메시지
        MessageEntity firstMessage = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );
        MessageEntity secondMessage = new MessageEntity(
                "KILL ME KILL ME",
                author,
                channel
        );
        List<MessageEntity> messages = List.of(firstMessage, secondMessage);
        messageRepository.saveAll(messages);

        // when
        List<MessageEntity> result = messageRepository.findByChannel(channel);

        // then
        assertEquals(2, result.size());
    }

    // [실패] 채널 미존재
    @Test
    @DisplayName("채널을 통한 다건 조회 실패: 메시지가 존재하지 않을 경우, 빈 리스트 반환")
    void find_all_with_details_by_channel_failure() {
        // given

        // 채널
        ChannelEntity channel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);

        // when
        List<MessageEntity> result = messageRepository.findByChannel(channel);

        // then
        assertTrue(result.isEmpty());
    }

    /*
        페이징 다건 조회
     */
    // [성공]
    @Test
    @DisplayName("첫번째 페이지 메시지 목록 조회 완료")
    void find_first_page_by_channel_id_success() {
        // given

        // 사용자
        UserEntity author = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(author);

        // 채널
        ChannelEntity channel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);

        // 메시지
        MessageEntity firstMessage = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );
        messageRepository.save(firstMessage);
        MessageEntity secondMessage = new MessageEntity(
                "KILL ME KILL ME",
                author,
                channel
        );
        messageRepository.save(secondMessage);

        // 페이지
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // when
        List<MessageEntity> result = messageRepository.findFirstPageByChannelId(channel.getId(), pageable);

        // then
        assertEquals(2, result.size());
        assertEquals(secondMessage.getId(), result.get(0).getId());
        assertEquals(secondMessage.getContent(), result.get(0).getContent());
    }

    // [실패] 메시지 미존재
    @Test
    @DisplayName("첫번쨰 페이지 메시지 목록 조회 실패: 메시지가 존재하지 않을 경우, 빈 리스트 반환")
    void find_first_page_by_channel_id_failure() {
        // given
        UUID channelId = UUID.randomUUID();

        // 페이지
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // when
        List<MessageEntity> result = messageRepository.findFirstPageByChannelId(channelId, pageable);

        // then
        assertTrue(result.isEmpty());
    }

    // [성공]
    @Test
    @DisplayName("다음 페이지 메시지 목록 조회 완료")
    void find_next_page_by_channel_id_success() {
        // given

        // 사용자
        UserEntity author = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(author);

        // 채널
        ChannelEntity channel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);

        // 메시지
        MessageEntity firstMessage = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );
        messageRepository.save(firstMessage);
        MessageEntity secondMessage = new MessageEntity(
                "KILL ME KILL ME",
                author,
                channel
        );
        messageRepository.save(secondMessage);

        // 페이지
        Instant cursor = Instant.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // when
        List<MessageEntity> result = messageRepository.findNextPageByChannelId(channel.getId(), cursor, pageable);

        // then
        assertEquals(2, result.size());
        assertEquals(secondMessage.getId(), result.get(0).getId());
        assertEquals(secondMessage.getContent(), result.get(0).getContent());
    }

    // [실패]
    @Test
    @DisplayName("다음 페이지 메시지 목록 조회 실패: 오래된 커서롲 조회할 경우, 빈 리스트 반환")
    void find_next_page_by_channel_id_failure() {
        // given
        UUID channelId = UUID.randomUUID();

        // 페이지
        Instant cursor = Instant.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // when
        List<MessageEntity> result = messageRepository.findNextPageByChannelId(channelId, cursor, pageable);

        // then
        assertTrue(result.isEmpty());
    }

    /*
        특정 채널에서 발행된 전체 메시지 개수 조회
     */
    // [성공]
    @Test
    @DisplayName("특정 채널에서 발행된 전체 메시지 개수 조회 완료")
    void count_all_by_channel_id_success() {
        // given

        // 사용자
        UserEntity author = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userRepository.save(author);

        // 채널
        ChannelEntity channel = new ChannelEntity(
                "Alien",
                "To be Alien together",
                ChannelType.PUBLIC
        );
        channelRepository.save(channel);

        // 메시지
        MessageEntity firstMessage = new MessageEntity(
                "LUV ME HATE ME",
                author,
                channel
        );
        MessageEntity secondMessage = new MessageEntity(
                "KILL ME KILL ME",
                author,
                channel
        );
        List<MessageEntity> messages = List.of(firstMessage, secondMessage);
        messageRepository.saveAll(messages);

        // when
        long result = messageRepository.countByChannelId(channel.getId());

        // then
        assertEquals(2, result);
    }

    // [실패]
    @Test
    @DisplayName("특정 채널에서 발행된 전체 메시지 개수 조회 실패: 메시지가 존재하지 않을 경우, 0 반환")
    void count_all_by_channel_id_failure() {
        // given
        UUID channelId = UUID.randomUUID();

        // when
        long result = messageRepository.countByChannelId(channelId);

        // then
        assertEquals(0L, result);
    }
}
