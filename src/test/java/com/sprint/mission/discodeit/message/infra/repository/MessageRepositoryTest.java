package com.sprint.mission.discodeit.message.infra.repository;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.infra.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import com.sprint.mission.discodeit.channel.infra.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

class MessageRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private BinaryContentRepository binaryContentRepository;

  private int userCounter = 0;

  @BeforeEach
  void setUp() {
    userCounter = 0;
    queryInspector.clear();
  }

  @Test
  @DisplayName(
      "message 생성에는 query 3개 생성된다\n"
          + "조회 시 query 1개 생성된다"
  )
  void success_to_create_and_find() {
    User user = createUser();
    Channel channel = createPublicChannel();
    List<BinaryContent> attachments = List.of(
        createBinaryContent(),
        createBinaryContent()
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
    Channel channel = createPublicChannel();
    Message message = Message.builder()
        .content("test")
        .author(user)
        .channel(channel)
        .attachments(List.of())
        .build();
    Assertions.assertThatThrownBy(() -> messageRepository.saveAndFlush(message))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("존재하지 않은 channel에서 message 생성할 수 없어서 예외를 던진다")
  void fail_to_create_due_to_not_existing_channel() {
    User user = createUser();
    Channel channel = em.getReference(Channel.class, UUID.randomUUID());
    Message message = Message.builder()
        .content("test")
        .author(user)
        .channel(channel)
        .attachments(List.of())
        .build();
    Assertions.assertThatThrownBy(() -> messageRepository.saveAndFlush(message))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("channel에서 생성된 모든 message를 조회한다")
  void success_to_find_all_by_channelId() {
    Channel channel = createPublicChannel();
    List<Message> expected = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      User author = createUser();
      Message message = Message.builder()
          .content("test" + i)
          .author(author)
          .channel(channel)
          .attachments(List.of())
          .build();
      expected.add(message);
    }
    messageRepository.saveAll(expected);
    em.flush();
    clear();
    Pageable pageable = PageRequest.of(0, 50, Sort.by(Direction.DESC, "createdAt"));
    Slice<Message> actual = messageRepository.findSliceByChannel_Id(channel.getId(), pageable);
    queryInspector.logQueries();
    System.out.println(
        "success_to_find_all_by_channelId expected count: " + queryInspector.getQueries().size());
    System.out.println("actual size: " + actual.getContent().size());
    System.out.println("expected size: " + expected.size());
    Assertions.assertThat(actual.getContent())
        .extracting(Message::getId, Message::getContent)
        .containsExactlyInAnyOrderElementsOf(
            expected.stream()
                .map(m -> tuple(m.getId(), m.getContent())).toList()
        );
  }

  @Test
  @DisplayName(
      "message를 삭제하면 연관된 BinaryContent, join table messages_attachments도 삭제 된다\n"
          + "user, channel은 삭제되지 않는다"
  )
  void success_to_delete() {
    User author = createUser();
    Channel channel = createPublicChannel();
    Message expected = createMessageWithAttachments(author, channel);
    Set<BinaryContent> attachments = expected.getAttachments();
    queryInspector.clear();
    messageRepository.delete(expected);
    em.flush();
    queryInspector.logQueries();
    System.out.println("success_to_delete count: " + queryInspector.getQueries().size());

    Assertions.assertThat(messageRepository.existsById(expected.getId())).isFalse();
    System.out.println("attachments to delete: " + attachments.size());
    attachments.forEach(a -> System.out.println(
        "attachment status: " + binaryContentRepository.findById(a.getId())
            .map(BinaryContent::getStatus).orElse(null)));
    Assertions.assertThat(attachments)
        .allMatch(a -> binaryContentRepository.findById(a.getId()).orElseThrow().getStatus()
            == com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus.DELETED);
    Assertions.assertThat(userRepository.existsById(author.getId())).isTrue();
    Assertions.assertThat(channelRepository.existsById(channel.getId())).isTrue();
    Assertions.assertThat(queryInspector.getQueries())
        .anyMatch(sql -> sql.contains("delete from message_attachments"));
  }

  @Test
  @DisplayName("message content 업데이트 할 수 있다")
  void success_to_update_content() {
    User author = createUser();
    Channel channel = createPublicChannel();
    Message expected = createMessageWithAttachments(author, channel);
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

  private User createUser() {
    userCounter++;
    User user = User.builder()
        .username("msguser" + userCounter)
        .email("msguser" + userCounter + "@test.com")
        .role(UserRole.USER)
        .build();
    return persistAndFlush(user);
  }

  private Channel createPublicChannel() {
    Channel channel = Channel.builder()
        .type(ChannelType.PUBLIC)
        .name("test-channel")
        .description("test description")
        .build();
    return persistAndFlush(channel);
  }

  private BinaryContent createBinaryContent() {
    return BinaryContent.builder()
        .fileName("test.txt")
        .size(100L)
        .contentType("text/plain")
        .build();
  }

  private Message createMessageWithAttachments(User author, Channel channel) {
    BinaryContent attachment = createBinaryContent();
    Message message = Message.builder()
        .content("test content")
        .author(author)
        .channel(channel)
        .attachments(List.of(attachment))
        .build();
    return persistAndFlush(message);
  }
}
