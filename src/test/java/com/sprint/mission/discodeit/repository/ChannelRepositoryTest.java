package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
@DataJpaTest(showSql = false)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // `application-test.yaml`에서 설정한 DB 정보대로 실행
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {
        readStatusRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser(String email, String username, String password, BinaryContent profile) {
        User author = new User(email, username, password, profile);

        return userRepository.save(author);
    }

    private Channel createChannel(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return channelRepository.save(channel);
    }

    @Test
    @DisplayName("채널 type이 PUBLIC이거나 사용자 ID와 연관된 ReadStatus가 존재하는 채널 목록을 조회할 수 있다.")
    void find_channel_list_with_userId_or_public_channel() {
        // given(준비)
        User user1 = createUser("test1@gmail.com", "test1", "1234", null);
        User user2 = createUser("test2@gmail.com", "test2", "1234", null);

        Channel channel1 = createChannel(ChannelType.PUBLIC, "test1Channel", "test1Channel입니다.");
        Channel channel2 = createChannel(ChannelType.PUBLIC, "test2Channel", "test2Channel입니다.");
        Channel channel3 = createChannel(ChannelType.PRIVATE, "test3Channel", "test3Channel입니다.");
        Channel channel4 = createChannel(ChannelType.PRIVATE, "test4Channel", "test4Channel입니다.");

        readStatusRepository.save(new ReadStatus(user1, channel1, Instant.now()));
        readStatusRepository.save(new ReadStatus(user1, channel2, Instant.now()));
        readStatusRepository.save(new ReadStatus(user1, channel3, Instant.now()));
        readStatusRepository.save(new ReadStatus(user2, channel4, Instant.now()));

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        List<Channel> result = channelRepository.findChannelByUserId(ChannelType.PUBLIC, user1.getId());

        // then(검증)
        assertEquals(3, result.size());
        assertThat(result)
                .extracting(channel -> channel.getId())
                .containsExactlyInAnyOrder(channel1.getId(), channel2.getId(), channel3.getId()) // 구성 원소와 개수가 정확히 동일해야 함
                .doesNotContain(channel4.getId());
    }

    @Test
    @DisplayName("채널 type이 PUBLIC이거나 사용자 ID와 연관된 ReadStatus가 없는 경우 빈 채널 목록을 조회할 수 있다.")
    void find_empty_channel_list_without_userId_or_public_channel() {
        // given(준비)
        User user1 = createUser("test1@gmail.com", "test1", "1234", null);
        User user2 = createUser("test2@gmail.com", "test2", "1234", null);

        Channel channel3 = createChannel(ChannelType.PRIVATE, "test3Channel", "test3Channel입니다.");
        Channel channel4 = createChannel(ChannelType.PRIVATE, "test4Channel", "test4Channel입니다.");

        readStatusRepository.save(new ReadStatus(user2, channel3, Instant.now()));
        readStatusRepository.save(new ReadStatus(user2, channel4, Instant.now()));

        // 영속성 해제
        testEntityManager.flush();
        testEntityManager.clear();

        // when(실행)
        List<Channel> result = channelRepository.findChannelByUserId(ChannelType.PUBLIC, user1.getId());

        // then(검증)
        assertEquals(0, result.size());
        assertThat(result)
                .extracting(channel -> channel.getId())
                .doesNotContain(channel3.getId(),  channel4.getId());
    }

}