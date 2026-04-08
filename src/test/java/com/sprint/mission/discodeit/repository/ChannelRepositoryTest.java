package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.TestJpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
public class ChannelRepositoryTest {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;
    @Autowired
    private UserRepository userRepository;

    private User targetUser;
    private Channel publicChannel;
    private Channel joinedPrivateChannel;
    private Channel notJoinedPrivateChannel;

    @BeforeEach
    void setUp() {
        targetUser = userRepository.save(new User("tester", "target@test.com", "asdf1234"));
        User otherUser = userRepository.save(new User("otherUser", "other@test.com", "pass"));

        publicChannel = channelRepository.save(new Channel(ChannelType.PUBLIC, "Public", "description"));

        joinedPrivateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, "Joined Private", "joined private description"));
        readStatusRepository.save(new ReadStatus(targetUser, joinedPrivateChannel));

        notJoinedPrivateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE, "Not Joined Private", "not joined private description"));
        readStatusRepository.save(new ReadStatus(otherUser, notJoinedPrivateChannel));

    }

    @Test
    @DisplayName("접근 가능한 public 채널과 본인이 참여한 private 채널만 조회할 수 있다.")
    void find_accessible_channels_by_userid_success() {
        // when
        List<Channel> accessibleChannels = channelRepository.findAccessibleChannelsByUserId(targetUser.getId());

        // then
        assertThat(accessibleChannels).hasSize(2);
        assertThat(accessibleChannels).extracting("id")
            .containsExactlyInAnyOrder(publicChannel.getId(), joinedPrivateChannel.getId());
        assertThat(accessibleChannels).extracting("id")
            .doesNotContain(notJoinedPrivateChannel.getId());
    }

    @Test
    @DisplayName("public 채널도 없고 참여한 private 채널도 없으면 빈 리스트를 반환한다.")
    void find_accessible_channels_by_userid_empty() {
        // given
        readStatusRepository.deleteAll();
        channelRepository.deleteAll();
        User isolatedUser = userRepository.save(new User("isolated", "iso@test.com", "pass"));
        channelRepository.save(new Channel(ChannelType.PRIVATE, "Secret", "Desc")); // 아무도 접근 불가한 PRIVATE 채널

        // when
        List<Channel> accessibleChannels = channelRepository.findAccessibleChannelsByUserId(isolatedUser.getId());

        // then
        assertThat(accessibleChannels).isEmpty();
    }

}
