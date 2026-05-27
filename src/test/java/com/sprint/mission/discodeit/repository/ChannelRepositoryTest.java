package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.Role;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
@AutoConfigureMockMvc(addFilters = false)
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("달선", "dalsun@naver.com", "ekftjs123", null, Role.USER);
        userRepository.save(user);
    }

    @Test
    @DisplayName("userId로 채널 조회 성공")
    void find_all_by_user_id_success_private_channel() {
        // given
        Channel privateChannel = new Channel(null, IsPrivate.PRIVATE, null);
        channelRepository.save(privateChannel);

        // when
        ReadStatus readStatus = new ReadStatus(user, privateChannel, Instant.now());
        readStatusRepository.save(readStatus);

        List<Channel> result = channelRepository.findAllByUserId(user.getId());

        // then
        assertThat(result).isNotEmpty();
        assertEquals(IsPrivate.PRIVATE, result.get(0).getType());
    }

    @Test
    @DisplayName("userId로 채널 조회 실패")
    void find_all_by_user_id_fail_private_channel() {
        // given
        Channel privateChannel = new Channel(null, IsPrivate.PRIVATE, null);
        channelRepository.save(privateChannel);

        // when
        List<Channel> result = channelRepository.findAllByUserId(user.getId());

        // then
        assertThat(result).isEmpty();
    }
}
