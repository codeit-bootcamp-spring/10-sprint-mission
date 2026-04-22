package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.config.TestJpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;


    private User author;
    private Channel channel;
    private Message msg1;
    private Message msg2;
    private Message msg3;

    @BeforeEach
    void setUp() throws InterruptedException {
        author = userRepository.save(new User("author", "author@test.com", "pass"));
        channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "Message Channel", "Desc"));

        // 최신순 정렬 및 커서 테스트를 위한 시간차 메시지 생성
        msg1 = messageRepository.save(new Message("First Message", channel, author));
        Thread.sleep(1000);
        msg2 = messageRepository.save(new Message("Second Message", channel, author));
        Thread.sleep(1000);
        msg3 = messageRepository.save(new Message("Third Message", channel, author));
    }

    @Test
    @DisplayName("특정 채널의 메시지를 페이지 크기에 맞게 slice로 가져올 수 있다.")
    void find_by_channel_id_slice_success() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2); // 2개씩 가져오기

        // when
        Slice<Message> slice = messageRepository.findByChannelId(channel.getId(), pageRequest);

        // then
        assertThat(slice.getContent()).hasSize(2); // 3개 중 2개만 가져왔는지 확인
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("특정 시간 이전에 작성된 메시지만 페이징하여 가져온다.")
    void find_by_channel_id_and_created_at_less_than_cursor_success() {
        // given
        Instant cursor = msg3.getCreatedAt().truncatedTo(ChronoUnit.MILLIS);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when (커서 시간 이전의 메시지들 조회)
        Slice<Message> slice = messageRepository.findByChannelIdAndCreatedAtLessThan(channel.getId(), cursor, pageRequest);

        // then
        assertThat(slice.getContent()).hasSize(2); // msg1, msg2만 가져와야 함
        assertThat(slice.getContent()).extracting("id")
            .containsExactlyInAnyOrder(msg1.getId(), msg2.getId());
    }

    @Test
    @DisplayName("특정 채널의 가장 최신 메시지 1건을 반환한다.")
    void find_top_by_channel_id_order_by_created_at_desc_success() {
        // when
        Optional<Message> topMessage = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channel.getId());

        // then
        assertThat(topMessage).isPresent();
        assertThat(topMessage.get().getId()).isEqualTo(msg3.getId()); // 가장 늦게 만든 msg3인지 확인
    }

}
