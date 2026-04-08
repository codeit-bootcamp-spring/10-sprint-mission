package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private MessageRepository messageRepository;

  @BeforeEach
  void clearData() {
    messageRepository.deleteAll();
    entityManager.flush();
  }

  @Test
  @DisplayName("채널 ID로 메시지 슬라이스 조회 성공 - 페이징 및 정렬")
  void findByChannelId_Success_PageAndSort() {
    // given
    User author = persistUser("ho", "ho@email.com");
    Channel channel = persistChannel("general-room");

    entityManager.persist(new Message(channel, author, "msg-a"));
    entityManager.persist(new Message(channel, author, "msg-b"));
    entityManager.persist(new Message(channel, author, "msg-c"));
    entityManager.flush();

    // when
    Slice<Message> slice = messageRepository.findByChannel_Id(
        channel.getId(),
        PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "content"))
    );

    // then
    assertThat(slice.getContent()).extracting(Message::getContent)
        .containsExactly("msg-a", "msg-b");
    assertThat(slice.hasNext()).isTrue();
  }

  @Test
  @DisplayName("채널 ID로 메시지 슬라이스 조회 실패 - 존재하지 않는 채널")
  void findByChannelId_Fail_NotFoundChannel() {
    // when
    Slice<Message> slice = messageRepository.findByChannel_Id(
        UUID.randomUUID(),
        PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "content"))
    );

    // then
    assertThat(slice.getContent()).isEmpty();
    assertThat(slice.hasNext()).isFalse();
  }

  @Test
  @DisplayName("기준 시각 이전 메시지 슬라이스 조회 성공")
  void findByChannelIdAndCreatedAtBefore_Success() {
    // given
    User author = persistUser("bo", "bo@email.com");
    Channel channel = persistChannel("timeline");

    entityManager.persist(new Message(channel, author, "first"));
    entityManager.persist(new Message(channel, author, "second"));
    entityManager.flush();

    // when
    Slice<Message> slice = messageRepository.findByChannel_IdAndCreatedAtBefore(
        channel.getId(),
        Instant.now().plusSeconds(1),
        PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "content"))
    );

    // then
    assertThat(slice.getContent()).hasSize(2);
  }

  @Test
  @DisplayName("기준 시각 이전 메시지 슬라이스 조회 실패 - 너무 과거 시각")
  void findByChannelIdAndCreatedAtBefore_Fail_TooOldCursor() {
    // given
    User author = persistUser("hh", "hh@email.com");
    Channel channel = persistChannel("history");
    entityManager.persist(new Message(channel, author, "message"));
    entityManager.flush();

    // when
    Slice<Message> slice = messageRepository.findByChannel_IdAndCreatedAtBefore(
        channel.getId(),
        Instant.EPOCH,
        PageRequest.of(0, 10)
    );

    // then
    assertThat(slice.getContent()).isEmpty();
  }

  @Test
  @DisplayName("작성자 ID로 메시지 목록 조회 성공")
  void findAllByAuthorId_Success() {
    // given
    User author = persistUser("hi", "hi@email.com");
    Channel channel = persistChannel("room");
    entityManager.persist(new Message(channel, author, "a1"));
    entityManager.persist(new Message(channel, author, "a2"));
    entityManager.flush();

    // when
    List<Message> messages = messageRepository.findAllByAuthor_Id(author.getId());

    // then
    assertThat(messages).hasSize(2);
  }

  @Test
  @DisplayName("작성자 ID로 메시지 목록 조회 실패 - 존재하지 않는 작성자")
  void findAllByAuthorId_Fail() {
    // when
    List<Message> messages = messageRepository.findAllByAuthor_Id(UUID.randomUUID());

    // then
    assertThat(messages).isEmpty();
  }

  private User persistUser(String name, String email) {
    User user = new User(name, email, "password123", null);
    entityManager.persist(user);
    return user;
  }

  private Channel persistChannel(String name) {
    Channel channel = new Channel(name, ChannelType.PUBLIC);
    entityManager.persist(channel);
    return channel;
  }
}
