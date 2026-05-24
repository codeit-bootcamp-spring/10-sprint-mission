package com.sprint.mission.discodeit.channel.repository;

import com.sprint.mission.discodeit.channel.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.support.fixture.ChannelFixture;
import com.sprint.mission.discodeit.support.generator.TestEntity;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled("BinaryContent schema 변경으로 인해 추후 수정")
class ChannelRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private TestEntity testEntity;
  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @BeforeEach
  void setUp() {
    queryInspector.clear();
  }

  @Test
  @DisplayName(
      """
          public channel 생성 후 조회 성공한다
          조회 시 query 1개 생성한다
          lastMessageAt은 존재한다
          """
  )
  void success_to_create_and_find_public() {
    Message message = testEntity.generatorMessage();
    Channel actual = message.getChannel();
    clear();
    ChannelDetailDto expected = channelRepository.findChannelDetailById(actual.getId())
        .orElseThrow();
    ensureQueryCount(1);
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("lastMessageAt")
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected);
    Assertions.assertThat(expected.lastMessageAt()).isNotNull();
  }

  @Test
  @DisplayName(
      "private channel 생성 후 조회 성공한다\n"
          + "조회 시 query 1개 생성한다"
  )
  void success_to_create_and_find_private() {
    Channel actual = testEntity.generatorPrivateChannel();
    ensureQueryCount(1);
    clear();
    ChannelDetailDto expected = channelRepository.findChannelDetailById(actual.getId())
        .orElseThrow();
    ensureQueryCount(1);
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected);
  }

  @Test
  @DisplayName(
      """
          public, private channel 을 생성한다
          모든 채널을 조회한다
          조회에 대한 query 1개 생성된다
          """
  )
  void findAllWithLastMessageAt() {
    List<Channel> actual = List.of(ChannelFixture.createPublic(), ChannelFixture.createPublic(),
        ChannelFixture.createPrivate(), ChannelFixture.createPrivate());
    channelRepository.saveAllAndFlush(actual);
    ensureQueryCount(1);
    clear();
    List<ChannelDetailDto> expected = channelRepository.findAllChannelDetails();
    ensureQueryCount(1);
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected);
  }

  @Test
  @DisplayName(
      "user 생성 및 private channel을 생성한다\n"
          + "user가 볼 수 있는 모든 channel(모든 public, user 가 속한 private channel)을 조회한다"
  )
  void findVisibleToWithLastMessageAt() {
    // given
    ReadStatus readStatus = testEntity.generatorReadStatus();
    User user = readStatus.getUser();
    Channel privateChannel = readStatus.getChannel();
    Channel publicChannel = testEntity.generatorPublicChannel();
    ensureQueryCount(4);
    clear();
    UUID userId = user.getId();
    List<UUID> expectedChannelIds = List.of(privateChannel.getId(), publicChannel.getId());

    // when
    List<ChannelDetailDto> actualChannelIds
        = channelRepository.findVisibleChannelDetails(userId);
    ensureQueryCount(1);

    // then
    Assertions.assertThat(actualChannelIds)
        .isEqualTo(expectedChannelIds);
  }

  @Test
  @DisplayName(
      "public channel 삭제 시 연관된 message만 삭제된다\n"
          + "이 message와 연관된 binary content은 삭제되지 않는다"
  )
  void success_to_delete_channel_with_messages_and_attachments() {
    Message message = testEntity.generatorMessage();
    queryInspector.logQueries();
    User user = message.getAuthor();
    Channel expected = message.getChannel();
    Set<BinaryContent> attachments = message.getAttachments();
    clear();
    channelRepository.deleteById(expected.getId());
    em.flush();
    queryInspector.logQueries();
    clear();

    Assertions.assertThat(channelRepository.existsById(expected.getId())).isFalse();
    Assertions.assertThat(messageRepository.existsById(message.getId())).isFalse();
    Assertions.assertThat(attachments)
        .extracting(BinaryContent::getId)
        .allMatch(binaryContentRepository::existsById);
    Assertions.assertThat(userRepository.existsById(user.getId())).isTrue();
  }
}
