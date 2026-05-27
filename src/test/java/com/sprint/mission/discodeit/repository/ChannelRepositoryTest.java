package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private TestEntityManager em;

    private User user1;
    private User user2;
    @BeforeEach
    void setUp() {
        user1 = new User("1번", "test@test.com", "12345678", null);
        new UserStatus(user1);
        em.persist(user1);

        user2 = new User("2번", "11@11",  "12345678", null);
        new UserStatus(user2);
        em.persist(user2);

        Channel channel1 = new Channel("공개 채널1", "테스트용이다.");
        em.persist(channel1);

        Channel channel2 = new Channel("공개 채널2", "공개 채널 2번");
        em.persist(channel2);

        Channel channel3 = Channel.createPrivateChannel();
        em.persist(channel3);

        ReadStatus readStatus = new ReadStatus(user1, channel3);
        em.persist(readStatus);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("userId로 Public + Private Channel을 찾는 쿼리문 입니다.")
    void findVisibleChannelsByUserId_성공() {
        // when
        List<Channel> channels = channelRepository.findVisibleChannelsByUserId(user1.getId());

        List<Channel> channels2 = channelRepository.findVisibleChannelsByUserId(user2.getId());
        // then (2 + 1)
        assertEquals(3, channels.size());
        // public만 조회
        assertEquals(2, channels2.size());
    }

    @Test
    @DisplayName("잘못된 userId로 쿼리 메서드 실행 시 빈 리스트가 반환됨.")
    void findVisibleChannelsByUserId_실패() {
        // when
        List<Channel> channels = channelRepository.findVisibleChannelsByUserId(UUID.randomUUID());

        System.out.println(channels.size());

        // then (Public Channel만 조회되어야 함)
        assertEquals(2, channels.size());
    }
}