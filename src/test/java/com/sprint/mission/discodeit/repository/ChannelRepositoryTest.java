package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("existsByName을 성공적으로 수행")
    void existsByName_success() {
        // given
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "desc"));

        // when
        boolean result = channelRepository.existsByName(channel.getName());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이름을 입력하여 existsByName 실패 검증")
    void existsByName_fail() {
        // given
        channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));
        // when
        // 유효하지 않은 이름으로 검색 -> false
        boolean result = channelRepository.existsByName("random");
        // then
        // 검증
        assertThat(result).isFalse();
    }

    @Test
    void findAllVisibleWithParticipants_success() {
        // given
        // me 유저 생성 및 영속화
        User me = userRepository.save(new User("me", "me@test.com", "pw", null));
        // other 유저 생성 및 영속화
        User other = userRepository.save(new User("other", "other@test.com", "pw", null));

        // 퍼블릭 채널 생성 및 영속화
        Channel publicChannel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "public-1", "desc"));

        // 사설 채널 생성 및 영속화
        Channel myPrivateChannel = channelRepository.save(
            new Channel(ChannelType.PRIVATE, "private-mine", "desc"));
        Channel othersPrivateChannel = channelRepository.save(
            new Channel(ChannelType.PRIVATE, "private-other", "desc"));

        readStatusRepository.save(new ReadStatus(me, myPrivateChannel));
        readStatusRepository.save(new ReadStatus(other, othersPrivateChannel));

        // when
        List<Channel> visible = channelRepository.findAllVisibleWithParticipants(me.getId(),
            ChannelType.PUBLIC);

        // then
        assertThat(visible)
            .extracting(Channel::getName)
            .contains("public-1", "private-mine")
            .doesNotContain("private-other");
    }

    @Test
    void findAllVisibleWithParticipants_fail_whenNothingVisible() {
        // given
        // me 유저 생성 및 영속화
        User me = userRepository.save(new User("me", "me@test.com", "pw", null));
        // other 유저 생성 및 영속화
        User other = userRepository.save(new User("other", "other@test.com", "pw", null));

        // Other의 사설 채널 생성 및 영속화
        Channel othersPrivateChannel = channelRepository.save(
            new Channel(ChannelType.PRIVATE, "private-other", "desc"));
        // 해당 채널과 other 유저의 readStatus 객체 생성 및 영속화
        readStatusRepository.save(new ReadStatus(other, othersPrivateChannel));

        // when
        // 채널 중에서 me가 접근하거나 볼 수 있는 채널 리스트를 조회한다. -> 없음
        List<Channel> visible = channelRepository.findAllVisibleWithParticipants(me.getId(),
            ChannelType.PUBLIC);

        // then
        // visible이 빈 리스트면 검증 통과
        assertThat(visible).isEmpty();
    }

    @Test
    void findWithParticipantsById_success() {
        // given
        // me 유저 객체 생성 및 영속화
        User me = userRepository.save(new User("me", "me@test.com", "pw", null));
        // 사설 채널 생성 및 영속화
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PRIVATE, "private-1", "desc"));
        // 해당 사설 채널에는 me 유저가 있음.
        readStatusRepository.save(new ReadStatus(me, channel));

        entityManager.flush();
        entityManager.clear();

        // when
        // 채널 ID로 채널과 연관 엔티티들을 로딩(Eager Load)하는 레포지토리 메서드 호출
        Channel found = channelRepository.findWithParticipantsById(channel.getId()).orElseThrow();

        // then
        // 채널의 ReadStatus의 사아즈가 하나면 검증 통과
        assertThat(found.getReadStatuses()).hasSize(1);
        // 채널의 readstatus에 담긴 유저 이름이 "me"와 동일하면 검증 통과
        assertThat(found.getReadStatuses().get(0).getUser().getUsername()).isEqualTo("me");
    }

    @Test
    void findWithParticipantsById_fail_whenNotFound() {
        // given
        // 존재하지 않을 id
        UUID unknownId = UUID.randomUUID();

        // when + then
        assertThat(channelRepository.findWithParticipantsById(unknownId)).isEmpty();
    }
}
