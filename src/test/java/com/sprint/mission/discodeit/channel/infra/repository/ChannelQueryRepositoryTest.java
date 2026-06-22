package com.sprint.mission.discodeit.channel.infra.repository;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.infra.repository.BinaryContentJpaRepository;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.infra.repository.MessageJpaRepository;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.infra.repository.ReadStatusJpaRepository;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.infra.repository.UserJpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sprint.mission.discodeit.channel.infra.repository.qdsl.ChannelQDSLRepository;
import org.springframework.context.annotation.Import;

@Import(ChannelQDSLRepository.class)
class ChannelQueryRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ChannelJpaRepository channelRepository;

  @Autowired
  private com.sprint.mission.discodeit.channel.domain.repository.ChannelQueryRepository channelQueryRepository;

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Autowired
  private ReadStatusJpaRepository readStatusRepository;

  @Autowired
  private MessageJpaRepository messageJpaRepository;

  @Autowired
  private BinaryContentJpaRepository binaryContentRepository;

  @BeforeEach
  void setUp() {
    queryInspector.clear();
  }

  @Test
  @DisplayName(
      """
          public channel 생성 후 조회 성공한다
          조회 시 query 2개 생성한다
          lastMessageAt은 존재한다
          """
  )
  void success_to_create_and_find_public() {
    User author = createUser("msgauthor", "msgauthor@test.com");
    Channel channel = createPublicChannel();
    Message message = createMessage(author, channel);
    clear();
    ChannelDetailDto expected = channelQueryRepository.findChannelDetailById(channel.getId())
        .orElseThrow();
    ensureQueryCount(2);
    Assertions.assertThat(channel)
        .usingRecursiveComparison()
        .ignoringFields("lastMessageAt")
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected.channel());
    Assertions.assertThat(expected.lastMessageAt()).isNotNull();
  }

  @Test
  @DisplayName(
      "private channel 생성 후 조회 성공한다\n"
          + "조회 시 query 2개 생성한다"
  )
  void success_to_create_and_find_private() {
    Channel actual = createPrivateChannel();
    ensureQueryCount(1);
    clear();
    ChannelDetailDto expected = channelQueryRepository.findChannelDetailById(actual.getId())
        .orElseThrow();
    ensureQueryCount(2);
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected.channel());
  }

  @Test
  @DisplayName(
      "user 생성 및 private channel을 생성한다\n"
          + "user가 볼 수 있는 모든 channel(모든 public, user 가 속한 private channel)을 조회한다"
  )
  void findVisibleToWithLastMessageAt() {
    // given
    User user = createUser("visibleuser", "visible@test.com");
    Channel privateChannel = createPrivateChannel();
    ReadStatus readStatus = createReadStatus(user, privateChannel);
    Channel publicChannel = createPublicChannel();
    System.out.println(
        "findVisibleToWithLastMessageAt expected query count: " + queryInspector.getQueries()
            .size());
    clear();
    UUID userId = user.getId();
    List<UUID> expectedChannelIds = List.of(privateChannel.getId(), publicChannel.getId());

    // when
    List<ChannelDetailDto> actualChannelIds
        = channelQueryRepository.findVisibleChannelDetails(userId);
    ensureQueryCount(2);

    // then
    Assertions.assertThat(actualChannelIds.stream().map(dto -> dto.channel().getId()).toList())
        .containsExactlyInAnyOrderElementsOf(expectedChannelIds);
  }

  @Test
  @DisplayName("public channel 삭제 시 연관된 message만 삭제된다\n이 message와 연관된 binary content은 삭제되지 않는다")
  void success_to_delete_channel_with_messages_and_attachments() {
    User author = createUser("deleteauthor", "deleteauthor@test.com");
    Channel expected = createPublicChannel();
    Message message = createMessage(author, expected);
    List<BinaryContent> attachments = message.getAttachments();
    clear();

    channelRepository.deleteById(expected.getId());
    em.flush();
    System.out.println(
        "delete_channel expected query count: " + queryInspector.getQueries().size());
    clear();

    Assertions.assertThat(channelRepository.existsById(expected.getId())).isFalse();
    Assertions.assertThat(messageJpaRepository.existsById(message.getId())).isFalse();
    Assertions.assertThat(attachments)
        .extracting(BinaryContent::getId)
        .allMatch(binaryContentRepository::existsById);
    Assertions.assertThat(userJpaRepository.existsById(author.getId())).isTrue();
  }

  private Channel createPublicChannel() {
    Channel channel = Channel.builder()
        .type(ChannelType.PUBLIC)
        .name("test-channel")
        .description("test description")
        .build();
    return persistAndFlush(channel);
  }

  private Channel createPrivateChannel() {
    Channel channel = Channel.builder()
        .type(ChannelType.PRIVATE)
        .build();
    return persistAndFlush(channel);
  }

  private User createUser(String username, String email) {
    User user = User.builder()
        .username(username)
        .email(email)
        .role(UserRole.USER)
        .build();
    return persistAndFlush(user);
  }

  private Message createMessage(User author, Channel channel) {
    BinaryContent attachment = BinaryContent.builder()
        .fileName("test.txt")
        .size(100L)
        .contentType("text/plain")
        .build();
    Message message = Message.builder()
        .content("test content")
        .author(author)
        .channel(channel)
        .attachments(List.of(attachment))
        .build();
    return persistAndFlush(message);
  }

  private ReadStatus createReadStatus(User user, Channel channel) {
    ReadStatus readStatus = ReadStatus.builder()
        .user(user)
        .channel(channel)
        .lastReadAt(Instant.now())
        .notificationEnabled(true)
        .build();
    return persistAndFlush(readStatus);
  }
}
