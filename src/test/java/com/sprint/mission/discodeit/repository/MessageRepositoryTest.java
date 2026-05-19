package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  private User author;
  private Channel channel;

  @BeforeEach
  void setUp() {
    author = new User("테스트유저", "test@email.com", "1234", null);
    userRepository.save(author);
    channel = new Channel(ChannelType.PUBLIC, "공개 채널", "공개 채널입니다.");
    channelRepository.save(channel);
  }

  @Test
  @DisplayName("특정 채널에서 가장 최근에 작성된 메시지를 하나 조회에 성공해야 한다.")
  void should_find_first_message_by_created_at() throws InterruptedException {
    // given
    Message message1 = new Message("첫 메시지", channel, author);
    Thread.sleep(10);
    Message message2 = new Message("두번째 메시지", channel, author);
    Thread.sleep(10);
    Message message3 = new Message("세번째 메시지", channel, author);
    messageRepository.save(message1);
    messageRepository.save(message2);
    messageRepository.save(message3);

    // when
    Optional<Message> result = messageRepository.findFirstByChannelOrderByCreatedAtDesc(
        channel);

    // then
    assertTrue(result.isPresent());
    assertEquals(message3.getId(), result.get().getId());
    assertEquals(message3.getContent(), result.get().getContent());
  }

  @Test
  @DisplayName("커서 기반 페이징이 적용된 메시지 목록을 조회한다,")
  void should_find_all_message_by_cursor() throws InterruptedException {
    // given
    Message message1 = new Message("첫 메시지", channel, author);
    Thread.sleep(10);
    Message message2 = new Message("두번째 메시지", channel, author);
    Thread.sleep(10);
    Message message3 = new Message("세번째 메시지", channel, author);
    messageRepository.save(message1);
    messageRepository.save(message2);
    messageRepository.save(message3);
    Instant cursor = message2.getCreatedAt();
    Pageable pageable = PageRequest.of(0, 10);

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdAndCreatedAtLessThan(
        channel.getId(), cursor, pageable);

    // then
    assertEquals(1, result.getContent().size());
    assertEquals(message1.getId(), result.getContent().get(0).getId());
    assertFalse(result.hasNext());
  }

  @Test
  @DisplayName("메시지가 없는 채널을 조회하면 빈 결과를 반환한다.")
  void should_return_empty_when_no_message() {
    // when
    Optional<Message> messageResult = messageRepository.findFirstByChannelOrderByCreatedAtDesc(
        channel);
    Slice<Message> sliceResult = messageRepository.findAllByChannelIdAndCreatedAtLessThan(
        channel.getId(), Instant.now(), PageRequest.of(0, 10));

    // then
    assertTrue(messageResult.isEmpty());
    assertTrue(sliceResult.getContent().isEmpty());
  }
}