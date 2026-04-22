package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.INSTANT;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private EntityManager em;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserRepository userRepository;

  @Nested
  class findByChannelIdOrderByCreatedAtDesc {

    @Test
    @DisplayName("페이징 처리로 제한된 메세지 반환")
    void should_return_limited_messages_with_pagination() throws Exception {
      // given
      User user = new User("A", "AA", "A@gmail.com");
      userRepository.save(user);
      Channel channel = Channel.of("name", "description");
      channelRepository.save(channel);

      int[] range = IntStream.rangeClosed(1, 10).toArray();
      for (int i : range) {
        messageRepository.save(new Message(channel, user, Integer.toString(i)));
        Thread.sleep(10);
      }
      int pageSize = 7;

      // when
      List<Message> messages = messageRepository.findByChannelIdOrderByCreatedAtDesc(
          channel.getId(), PageRequest.of(0, pageSize, Sort.by(Direction.DESC, "createdAt")));

      // then
      assertThat(messages)
          .hasSize(pageSize)
          .extracting(Message::getCreatedAt)
          .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("채널에 메세지가 없는 경우 빈 리스트 반환")
    void should_return_empty_when_message_is_not_existing_in_channel() {
      // when
      List<Message> messages = messageRepository.findByChannelIdOrderByCreatedAtDesc(
          UUID.randomUUID(), PageRequest.of(0, 7));

      // then
      assertThat(messages).isEmpty();

    }

  }

  @Nested
  class findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc {

    @Test
    @DisplayName("커서를 기준으로 그보다 이전에 작성된 메세지를 최신순으로 반환")
    void should_return_messages_older_than_cursor() throws Exception {
      // given
      User user = new User("A", "AA", "A@gmail.com");
      userRepository.save(user);
      Channel channel = Channel.of("name", "description");
      channelRepository.save(channel);

      int[] range = IntStream.rangeClosed(1, 10).toArray();
      for (int i : range) {
        messageRepository.save(new Message(channel, user, Integer.toString(i)));
        Thread.sleep(100);
      }
      messageRepository.flush();
      em.clear();  // DB의 Instant 정밀도를 활용하기 위해 1차 영속성 캐시 비우기(Java의 Instant는 나노단위, DB는 마이크로 단위)

      int pageSize = 7;
      Pageable page = PageRequest.of(0, pageSize, Sort.by(Direction.DESC, "createdAt"));
      List<Message> firstPage = messageRepository.findByChannelIdOrderByCreatedAtDesc(
          channel.getId(), page);
      Instant cursor = firstPage.get(firstPage.size() - 1).getCreatedAt();

      // when
      List<Message> messages = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
          channel.getId(), cursor, page);

      // then
      assertThat(messages)
          .extracting(Message::getCreatedAt)
          .isSortedAccordingTo(Comparator.reverseOrder());

      assertThat(messages)
          .first()
          .extracting(Message::getCreatedAt)
          .asInstanceOf(INSTANT)
          .isBefore(cursor);

    }

    @Test
    @DisplayName("다음 메세지가 없는 경우 빈 리스트 반환")
    void should_return_empty_when_no_older_messages_exist() throws Exception {
      // given
      User user = new User("A", "AA", "A@gmail.com");
      userRepository.save(user);
      Channel channel = Channel.of("name", "description");
      channelRepository.save(channel);

      Message msg1 = new Message(channel, user, "first");
      messageRepository.save(msg1);
      ReflectionTestUtils.setField(msg1, "createdAt", Instant.now().minusSeconds(10));
      messageRepository.saveAndFlush(msg1);

      Message latest = new Message(channel, user, "latest");
      messageRepository.save(latest);
      em.clear();

      int pageSize = 2;
      Pageable page = PageRequest.of(0, pageSize, Sort.by(Direction.DESC, "createdAt"));
      List<Message> firstPage = messageRepository.findByChannelIdOrderByCreatedAtDesc(
          channel.getId(), page);
      Instant cursor = firstPage.get(firstPage.size() - 1).getCreatedAt();

      // when
      List<Message> messages = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
          channel.getId(), cursor, page);

      // then
      assertThat(messages).isEmpty();
    }

  }

  @Nested
  class findFirstByChannelIdOrderByCreatedAtDesc {

    @Test
    @DisplayName("생성 시간이 제일 최근것인 메세지 반환")
    void should_return_latest_message_optional() throws Exception {
      // given
      User user = new User("A", "AA", "A@gmail.com");
      userRepository.save(user);
      Channel channel = Channel.of("name", "description");
      channelRepository.save(channel);

      Message msg1 = new Message(channel, user, "first");
      messageRepository.save(msg1);
      Thread.sleep(100);
      Message latest = new Message(channel, user, "latest");
      messageRepository.save(latest);

      // when
      Optional<Message> found = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(
          channel.getId());

      // then
      assertThat(found).hasValueSatisfying(m -> {
        assertThat(m.getContent()).isEqualTo(latest.getContent());
        assertThat(m.getCreatedAt()).isEqualTo(latest.getCreatedAt());
      });

    }

    @Test
    @DisplayName("메세지 없을 때의 빈 객체 반환")
    void should_return_empty_when_no_messages() {
      // when
      Optional<Message> found = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(
          UUID.randomUUID());

      // then
      assertThat(found).isEmpty();
    }
  }
}