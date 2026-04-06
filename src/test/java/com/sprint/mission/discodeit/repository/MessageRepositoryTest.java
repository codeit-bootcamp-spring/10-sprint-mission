package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MessageRepository 슬라이스 테스트")
public class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    private User savedUser() {
        User user = userRepository.save(new User("tester", "tester@test.com", "1234", null));
        userStatusRepository.save(new UserStatus(user, Instant.now()));
        return user;
    }

    private Channel savedChannel() {
        return channelRepository.save(new  Channel(ChannelType.PUBLIC, "일반", "기본 채널"));
    }

    @Nested
    @DisplayName("최신 메세지 조회 테스트")
    class FindTopMessageTest {

        @Test
        @DisplayName("채널에 메시지가 여러 개 있으면 가장 최신 메시지를 반환한다")
        void should_return_latest_message_when_exists () {
            // given
            Channel channel = savedChannel();
            User user = savedUser();

            messageRepository.save(new Message("1", channel, user, Collections.emptyList()));
            messageRepository.save(new Message("2", channel, user, Collections.emptyList()));

            // when -> 최신순으로 가져올떄
            Optional<Message> result =
                    messageRepository.findTopByChannel_IdOrderByCreatedAtDesc(channel.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getContent()).isEqualTo("2");
        }
    }


}
