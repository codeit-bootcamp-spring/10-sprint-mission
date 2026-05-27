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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private Channel channel;
    private Channel channel2;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User("전승주", "jsj@naver.com", "12345678", null);
        new UserStatus(user1);  // cascade로 함께 저장
        em.persist(user1);

        user2 = new User("홍길동", "hong@naver.com", "12345678", null);
        new UserStatus(user2);
        em.persist(user2);

        user3 = new User("몰라", "몰라@몰라", "12345678", null);
        new UserStatus(user3);
        em.persist(user3);

        channel = new Channel("테스트 채널", "설명");
        em.persist(channel);

        channel2 = Channel.createPrivateChannel();
        em.persist(channel2);

        ReadStatus readStatus = new ReadStatus(user1, channel2);
        em.persist(readStatus);


        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("채널 참여 유저 조회 - user1만 반환")
    void findAllByChannelId_성공() {
        // given, when
        List<User> result  = userRepository.findAllByChannelId(channel2.getId());
        // then
        assertEquals(1, result.size());
        assertEquals(user1.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("채널 참여 유저 조회 실패 - 아무도 참여하지 않은 채널이면 빈 리스트 반환")
    void findAllByChannelId_실패() {
        // given: channel은 아무도 ReadStatus가 없음
        // when
        List<User> result = userRepository.findAllByChannelId(channel.getId());
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("여러 채널 ID로 ReadStatus 조회 성공 - channel2에 user1의 ReadStatus가 반환됨")
    void findAllByChannelIdIn_성공() {
        // given
        List<UUID> channelIds = List.of(channel.getId(), channel2.getId());

        // when
        List<ReadStatus> result = userRepository.findAllByChannelIdIn(channelIds);

        // then: setUp에서 user1 → channel2 ReadStatus 1개만 존재
        assertEquals(1, result.size());
        assertEquals(user1.getId(), result.get(0).getUser().getId());
        assertEquals(channel2.getId(), result.get(0).getChannel().getId());
    }

    @Test
    @DisplayName("여러 채널 ID로 ReadStatus 조회 실패 - 존재하지 않는 채널 ID면 빈 리스트 반환")
    void findAllByChannelIdIn_실패() {
        // given
        List<UUID> channelIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        // when
        List<ReadStatus> result = userRepository.findAllByChannelIdIn(channelIds);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("모든 유저 프로필·상태 포함 조회 성공 - 3명 모두 반환")
    void findAllWithProfileAndStatus_성공() {
        // given, when
        List<User> result = userRepository.findAllWithProfileAndStatus();

        // then
        assertEquals(3, result.size());
        // userStatus가 함께 로드되었는지 확인 (Lazy 로딩 없이 바로 접근 가능)
        result.forEach(u -> assertNotNull(u.getUserStatus()));
    }

    @Test
    @DisplayName("유저명으로 프로필 조회 성공 - 일치하는 유저 반환")
    void findByUsernameWithProfile_성공() {
        // given, when
        Optional<User> result = userRepository.findByUsernameWithProfile("전승주");

        // then
        assertTrue(result.isPresent());
        assertEquals("jsj@naver.com", result.get().getEmail());
    }

    @Test
    @DisplayName("유저명으로 프로필 조회 실패 - 존재하지 않는 이름이면 Optional.empty 반환")
    void findByUsernameWithProfile_실패() {
        // given, when
        Optional<User> result = userRepository.findByUsernameWithProfile("없는유저");

        // then
        assertTrue(result.isEmpty());
    }
}
