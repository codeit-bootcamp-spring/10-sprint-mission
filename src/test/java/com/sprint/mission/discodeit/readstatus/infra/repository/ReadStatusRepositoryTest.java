package com.sprint.mission.discodeit.readstatus.infra.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReadStatusRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private ReadStatusJpaRepository readStatusRepository;

  private int userCounter = 0;

  @BeforeEach
  void setUp() {
    userCounter = 0;
    queryInspector.clear();
  }

  @Test
  @DisplayName("save and findAllByUserId - 유저 ID로 읽음 상태를 정상 조회한다.")
  void save_and_findAllByUserId() {
    ReadStatus readStatus = createReadStatus();
    User user = readStatus.getUser();
    flushAndClear();
    queryInspector.clear();

    List<ReadStatus> result = readStatusRepository.findAllByUser_Id(user.getId());

    queryInspector.logQueries();
    System.out.println("findAllByUserId query count: " + queryInspector.getQueries().size());
    
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getUser().getId()).isEqualTo(user.getId());
  }

  @Test
  @DisplayName("existsByChannel_IdAndUser_Id - 채널과 유저 ID로 존재 여부를 정상 확인한다.")
  void existsByChannelIdAndUserId_true() {
    ReadStatus readStatus = createReadStatus();
    User user = readStatus.getUser();
    Channel channel = readStatus.getChannel();
    flushAndClear();
    queryInspector.clear();

    boolean exists = readStatusRepository.existsByChannel_IdAndUser_Id(channel.getId(), user.getId());

    queryInspector.logQueries();
    System.out.println("existsByChannel_IdAndUser_Id query count: " + queryInspector.getQueries().size());

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("countByChannel_IdAndUser_IdIn - 특정 채널의 다수 유저들의 읽음 상태 개수를 정상 확인한다.")
  void countByChannelIdAndUserIdIn() {
    ReadStatus readStatus = createReadStatus();
    User user = readStatus.getUser();
    Channel channel = readStatus.getChannel();
    flushAndClear();
    queryInspector.clear();

    long count = readStatusRepository.countByChannel_IdAndUser_IdIn(channel.getId(), List.of(user.getId()));

    queryInspector.logQueries();
    System.out.println("countByChannel_IdAndUser_IdIn query count: " + queryInspector.getQueries().size());

    assertThat(count).isEqualTo(1L);
  }

  private User createUser() {
    userCounter++;
    User user = User.builder()
        .username("rsuser" + userCounter)
        .email("rsuser" + userCounter + "@test.com")
        .role(UserRole.USER)
        .build();
    return persistAndFlush(user);
  }

  private Channel createPrivateChannel() {
    Channel channel = Channel.builder()
        .type(ChannelType.PRIVATE)
        .build();
    return persistAndFlush(channel);
  }

  private ReadStatus createReadStatus() {
    User user = createUser();
    Channel channel = createPrivateChannel();
    ReadStatus readStatus = ReadStatus.builder()
        .user(user)
        .channel(channel)
        .lastReadAt(Instant.now())
        .notificationEnabled(true)
        .build();
    return persistAndFlush(readStatus);
  }
}
