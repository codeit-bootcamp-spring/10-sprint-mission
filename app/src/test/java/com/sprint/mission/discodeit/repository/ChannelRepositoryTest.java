package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sprint.mission.discodeit.dto.ChannelDto.ChannelSummary;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Nested
  class findAllByUserId {

    @Test
    @DisplayName("유저가 public 채널과 참여중인 private 채널 객체 반환")
    void should_return_public_and_participating_private_channels() {
      // given
      User user1 = new User("A", "AA", "A@gmail.com");
      User user2 = new User("B", "BB", "B@naver.com");
      userRepository.saveAll(List.of(user1, user2));

      Channel publicChannel = Channel.of("pub1", null);
      Channel privateChannel = Channel.of(List.of(user1.getId(), user2.getId()));
      channelRepository.saveAll(List.of(publicChannel, privateChannel));

      // sql 조건에 readstatus도 포함
      ReadStatus readStatus1 = new ReadStatus(user1, privateChannel);
      ReadStatus readStatus2 = new ReadStatus(user2, privateChannel);
      readStatusRepository.saveAll(List.of(readStatus1, readStatus2));

      // when
      List<ChannelSummary> actual = channelRepository.findAllByUserId(user1.getId());

      // then
      assertThat(actual)
          .hasSize(2)
          .extracting(ChannelSummary::type)
          .contains(ChannelType.PUBLIC, ChannelType.PRIVATE);
    }

    @Test
    @DisplayName("유저가 존재하지 않아도 public 채널은 반환")
    void should_return_only_public_channel_when_userId_does_not_exist() {
      // when
      Channel publicChannel = Channel.of("pub1", null);
      channelRepository.save(publicChannel);

      List<ChannelSummary> actual = channelRepository.findAllByUserId(UUID.randomUUID());

      // then
      assertEquals(1, actual.size());
    }
  }
}