package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class ReadStatusRepositoryTest {

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager entityManager;

  private User createTestUser(String username, String email) {
    BinaryContent profile = new BinaryContent("profile.jpg", 1024L, "image/jpeg");
    User user = new User(username, email, "password123!@#", profile, UserRole.USER);
    return userRepository.save(user);
  }

  private Channel createTestChannel(ChannelType type, String name) {
    Channel channel = new Channel(type, name, "설명: " + name);
    return channelRepository.save(channel);
  }

  private ReadStatus createTestReadStatus(User user, Channel channel, Instant lastReadAt) {
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    return readStatusRepository.save(readStatus);
  }

  @Test
  @DisplayName("사용자 ID로 모든 읽음 상태를 조회할 수 있다")
  void findAllByUserId_ReturnsReadStatuses() {

    User user = createTestUser("testUser", "test@example.com");
    Channel channel1 = createTestChannel(ChannelType.PUBLIC, "채널1");
    Channel channel2 = createTestChannel(ChannelType.PRIVATE, "채널2");

    Instant now = Instant.now();
    ReadStatus readStatus1 = createTestReadStatus(user, channel1, now.minus(1, ChronoUnit.DAYS));
    ReadStatus readStatus2 = createTestReadStatus(user, channel2, now);

    entityManager.flush();
    entityManager.clear();

    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(user.getId());

    assertThat(readStatuses).hasSize(2);
  }

  @Test
  @DisplayName("채널 ID로 모든 읽음 상태를 사용자 정보와 함께 조회할 수 있다")
  void findAllByChannelIdWithUser_ReturnsReadStatusesWithUser() {

    User user1 = createTestUser("user1", "user1@example.com");
    User user2 = createTestUser("user2", "user2@example.com");
    Channel channel = createTestChannel(ChannelType.PUBLIC, "공개채널");

    Instant now = Instant.now();
    ReadStatus readStatus1 = createTestReadStatus(user1, channel, now.minus(1, ChronoUnit.DAYS));
    ReadStatus readStatus2 = createTestReadStatus(user2, channel, now);

    entityManager.flush();
    entityManager.clear();

    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(
        channel.getId());

    assertThat(readStatuses).hasSize(2);

    for (ReadStatus status : readStatuses) {
      assertThat(Hibernate.isInitialized(status.getUser())).isTrue();
      assertThat(Hibernate.isInitialized(status.getUser().getProfile())).isTrue();
    }
  }

  @Test
  @DisplayName("사용자 ID와 채널 ID로 읽음 상태 존재 여부를 확인할 수 있다")
  void existsByUserIdAndChannelId_ExistingStatus_ReturnsTrue() {

    User user = createTestUser("testUser", "test@example.com");
    Channel channel = createTestChannel(ChannelType.PUBLIC, "공개채널");

    ReadStatus readStatus = createTestReadStatus(user, channel, Instant.now());

    entityManager.flush();
    entityManager.clear();

    Boolean exists = readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
        .isPresent();

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 읽음 상태에 대해 false를 반환한다")
  void existsByUserIdAndChannelId_NonExistingStatus_ReturnsFalse() {

    User user = createTestUser("testUser", "test@example.com");
    Channel channel = createTestChannel(ChannelType.PUBLIC, "공개채널");

    entityManager.flush();
    entityManager.clear();

    Boolean exists = readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
        .isPresent();

    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("채널의 모든 읽음 상태를 삭제할 수 있다")
  void deleteAllByChannelId_DeletesAllReadStatuses() {

    User user1 = createTestUser("user1", "user1@example.com");
    User user2 = createTestUser("user2", "user2@example.com");

    Channel channel = createTestChannel(ChannelType.PUBLIC, "삭제할채널");
    Channel otherChannel = createTestChannel(ChannelType.PUBLIC, "유지할채널");

    createTestReadStatus(user1, channel, Instant.now());
    createTestReadStatus(user2, channel, Instant.now());

    createTestReadStatus(user1, otherChannel, Instant.now());

    entityManager.flush();
    entityManager.clear();

    readStatusRepository.deleteAllByChannelId(channel.getId());
    entityManager.flush();
    entityManager.clear();

    List<ReadStatus> channelReadStatuses = readStatusRepository.findAllByChannelIdWithUser(
        channel.getId());
    assertThat(channelReadStatuses).isEmpty();

    List<ReadStatus> otherChannelReadStatuses = readStatusRepository.findAllByChannelIdWithUser(
        otherChannel.getId());
    assertThat(otherChannelReadStatuses).hasSize(1);
  }
}
