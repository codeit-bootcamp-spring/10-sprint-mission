package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.generator.TestEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Disabled("BinaryContent schema 변경으로 인해 추후 변경")
class MessageRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private BinaryContentRepository binaryContentRepository;
  @Autowired
  private TestEntity testEntity;

  @BeforeEach
  void setUp() {
    queryInspector.clear();
  }

  @Test
  @DisplayName(
      "message 생성에는 query 3개 생성된다\n"
          + "조회 시 query 2개 생성된다"
  )
  void success_to_create_and_find() {
    User user = testEntity.generatorUser();
    Channel channel = testEntity.generatorPublicChannel();
    List<BinaryContent> attachments = List.of(
        BinaryContentFixture.createEntity(),
        BinaryContentFixture.createEntity()
    );
    queryInspector.clear();
    Message expected = Message.builder()
        .author(user)
        .channel(channel)
        .attachments(attachments)
        .build();
    messageRepository.saveAndFlush(expected);
    queryInspector.logQueries();
    ensureQueryCount(3);
    clear();

    Message actual = messageRepository.findWithFetchJoinById(expected.getId()).orElseThrow();
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected);
    queryInspector.logQueries();
    ensureQueryCount(1);
  }

  @Test
  @DisplayName("존재하지 않은 user는 message 생성할 수 없어서 예외를 던진다")
  void fail_to_create_due_to_not_existing_user() {
    User user = em.getReference(User.class, UUID.randomUUID());
    Channel channel = testEntity.generatorPublicChannel();
    Message message = MessageFixture.createEntity(user, channel, List.of());
    Assertions.assertThatThrownBy(() -> messageRepository.saveAndFlush(message))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("존재하지 않은 channel에서 message 생성할 수 없어서 예외를 던진다")
  void fail_to_create_due_to_not_existing_channel() {
    User user = testEntity.generatorUser();
    Channel channel = em.getReference(Channel.class, UUID.randomUUID());
    Message message = MessageFixture.createEntity(user, channel, List.of());
    Assertions.assertThatThrownBy(() -> messageRepository.saveAndFlush(message))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("channel에서 생성된 모든 message를 조회한다")
  void success_to_find_all_by_channelId() {
    Channel channel = testEntity.generatorPublicChannel();
    List<Message> expected = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      User author = testEntity.generatorUser();
      Message message = MessageFixture.createEntity(author, channel, List.of());
      expected.add(message);
    }
    messageRepository.saveAll(expected);
    em.flush();
    clear();
    Pageable pageable = PageRequest.of(0, 50, Sort.by(Direction.DESC, "createdAt"));
    Slice<Message> actual = messageRepository.findSliceByChannelId(channel.getId(), pageable);
    Assertions.assertThat(actual.getContent())
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected);
    queryInspector.logQueries();
    ensureQueryCount(1);
  }

  @Test
  @DisplayName(
      "message를 삭제하면 연관된 BinaryContent, join table messages_attachments도 삭제 된다\n"
          + "user, channel은 삭제되지 않는다"
  )
  void success_to_delete() {
    Message expected = testEntity.generatorMessage();
    User author = expected.getAuthor();
    Channel channel = expected.getChannel();
    Set<BinaryContent> attachments = expected.getAttachments();
    queryInspector.clear();
    messageRepository.delete(expected);
    em.flush();
    queryInspector.logQueries();
    ensureQueryCount(3);

    Assertions.assertThat(messageRepository.existsById(expected.getId())).isFalse();
    Assertions.assertThat(attachments)
        .extracting(BinaryContent::getId)
        .allMatch(Predicate.not(binaryContentRepository::existsById));
    Assertions.assertThat(userRepository.existsById(author.getId())).isTrue();
    Assertions.assertThat(channelRepository.existsById(channel.getId())).isTrue();
    Assertions.assertThat(queryInspector.getQueries())
        .anyMatch(sql -> sql.contains("delete from message_attachments"));
  }

  @Test
  @DisplayName("message content 업데이트 할 수 있다")
  void success_to_update_content() {
    Message expected = testEntity.generatorMessage();
    queryInspector.clear();
    String oldValue = expected.getContent();
    String newValue = "ha ha hah a";
    expected.setContent(newValue);
    flushAndClear();
    queryInspector.logQueries();
    ensureQueryCount(1);

    Message actual = messageRepository.findWithFetchJoinById(expected.getId()).orElseThrow();
    Assertions.assertThat(actual.getContent())
        .isNotEqualTo(oldValue)
        .isEqualTo(newValue);
  }
}
