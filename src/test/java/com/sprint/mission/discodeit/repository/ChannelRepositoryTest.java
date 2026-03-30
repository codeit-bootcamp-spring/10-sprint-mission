package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class ChannelRepositoryTest {

  @Autowired
  ChannelRepository channelRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ReadStatusRepository readStatusRepository;

  @Test
  @DisplayName("PUBLIC 채널은 참여 여부와 무관하게 조회되어야 한다")
  void should_find_all_public_channels() {
    // given
    User fixedUser = new User("테스트유저", "test@email.com", "1234", null);
    userRepository.save(fixedUser);
    Channel publicChannel = new Channel(ChannelType.PUBLIC, "공개 채널", "공개 채널입니다.");
    channelRepository.save(publicChannel);

    // when
    List<Channel> channels = channelRepository.findAllByUserId(fixedUser.getId());

    // then
    assertEquals(1, channels.size());
    assertEquals(publicChannel.getId(), channels.get(0).getId());
  }

  @Test
  @DisplayName("유저가 참여 중인 PRIVATE 채널은 조회되어야 한다.")
  void should_find_all_private_channels_when_user_participant() {
    // given
    User fixedUser = new User("테스트유저", "test@email.com", "1234", null);
    userRepository.save(fixedUser);
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(privateChannel);
    ReadStatus fixedReadStatus = new ReadStatus(fixedUser, privateChannel, Instant.now());
    readStatusRepository.save(fixedReadStatus);

    // when
    List<Channel> channels = channelRepository.findAllByUserId(fixedUser.getId());

    // then
    assertEquals(1, channels.size());
    assertEquals(privateChannel.getId(), channels.get(0).getId());
  }

  @Test
  @DisplayName("유저가 참여하지 않은 PRIVATE 채널은 조회되지 않아야 한다.")
  void should_return_empty_list_when_user_is_not_participant_in_private_channel() {
    // given
    User fixedUser = new User("테스트유저", "test@email.com", "1234", null);
    userRepository.save(fixedUser);
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(privateChannel);

    // when
    List<Channel> channels = channelRepository.findAllByUserId(fixedUser.getId());

    // then
    assertEquals(0, channels.size());
  }
}