package com.sprint.mission.discodeit.message.infra.repository;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.infra.repository.BinaryContentJpaRepository;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import com.sprint.mission.discodeit.channel.infra.repository.ChannelJpaRepository;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.infra.repository.UserJpaRepository;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

class MessageJpaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private UserJpaRepository userJpaRepository;
  @Autowired
  private ChannelJpaRepository channelRepository;
  @Autowired
  private MessageJpaRepository messageJpaRepository;
  @Autowired
  private BinaryContentRepository binaryContentRepository;

  private int userCounter = 0;

  @BeforeEach
  void setUp() {
    userCounter = 0;
    queryInspector.clear();
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
    Assertions.assertThatThrownBy(() -> messageJpaRepository.saveAndFlush(message))
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
    Assertions.assertThatThrownBy(() -> messageJpaRepository.saveAndFlush(message))
        .isInstanceOf(DataIntegrityViolationException.class);
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
    List<BinaryContent> attachments = expected.getAttachments();
    queryInspector.clear();
    messageJpaRepository.delete(expected);
    em.flush();
    queryInspector.logQueries();
    System.out.println("success_to_delete count: " + queryInspector.getQueries().size());

    Assertions.assertThat(messageJpaRepository.existsById(expected.getId())).isFalse();
    System.out.println("attachments to delete: " + attachments.size());
    attachments.forEach(a -> System.out.println(
        "attachment status: " + binaryContentRepository.findById(
                a.getId())
            .map(BinaryContent::getStatus).orElse(null)));
    Assertions.assertThat(attachments)
        .allMatch(a ->
            binaryContentRepository.findById(a.getId()).orElseThrow()
                .getStatus()
                == com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus.DELETED);
    Assertions.assertThat(userJpaRepository.existsById(author.getId())).isTrue();
    Assertions.assertThat(channelRepository.existsById(channel.getId())).isTrue();
    Assertions.assertThat(queryInspector.getQueries())
        .anyMatch(sql -> sql.contains("delete from message_attachments"));
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
