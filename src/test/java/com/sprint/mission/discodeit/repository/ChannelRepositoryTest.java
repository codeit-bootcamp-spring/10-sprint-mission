package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("유저별 Visible 채널 조회 성공")
    void findVisibleChannels_success() {
        User requester = persistUser("requester");
        User another = persistUser("another");
        Channel publicChannel = persistChannel(Channel.buildPublic("general", "public"));
        Channel privateVisible = persistChannel(Channel.buildPrivate());
        Channel privateHidden = persistChannel(Channel.buildPrivate());

        entityManager.persist(new ReadStatus(requester, privateVisible, Instant.now()));
        entityManager.persist(new ReadStatus(another, privateHidden, Instant.now()));
        flushAndClear();

        List<Channel> channels = channelRepository.findVisibleChannels(
                requester.getId(),
                ChannelType.PUBLIC,
                ChannelType.PRIVATE
        );

        assertThat(channels).extracting(Channel::getId)
                .contains(publicChannel.getId(), privateVisible.getId())
                .doesNotContain(privateHidden.getId());
    }

    @Test
    @DisplayName("비참여 PRIVATE 채널은 조회 실패")
    void findVisibleChannels_fail_forPrivateChannelWithoutMembership() {
        User requester = persistUser("requester");
        User another = persistUser("another");
        Channel privateChannel = persistChannel(Channel.buildPrivate());
        entityManager.persist(new ReadStatus(another, privateChannel, Instant.now()));
        flushAndClear();

        List<Channel> channels = channelRepository.findVisibleChannels(
                requester.getId(),
                ChannelType.PUBLIC,
                ChannelType.PRIVATE
        );

        assertThat(channels).isEmpty();
    }

    @Test
    @DisplayName("채널 페이징 및 정렬 조회 성공")
    void findAll_success_withPagingAndSort() {
        persistChannel(Channel.buildPublic("charlie", "desc"));
        persistChannel(Channel.buildPublic("alpha", "desc"));
        persistChannel(Channel.buildPublic("bravo", "desc"));
        flushAndClear();

        Page<Channel> page = channelRepository.findAll(
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"))
        );

        assertThat(page.getContent()).extracting(Channel::getName)
                .containsExactly("alpha", "bravo");
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    @DisplayName("범위를 벗어난 채널 페이지 조회 실패")
    void findAll_fail_whenPageOutOfRange() {
        persistChannel(Channel.buildPublic("alpha", "desc"));
        flushAndClear();

        Page<Channel> page = channelRepository.findAll(
                PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "name"))
        );

        assertThat(page.getContent()).isEmpty();
    }

    private User persistUser(String username) {
        User user = new User(username, "1234", username + "@test.com");
        entityManager.persist(user);
        return user;
    }

    private Channel persistChannel(Channel channel) {
        entityManager.persist(channel);
        return channel;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
