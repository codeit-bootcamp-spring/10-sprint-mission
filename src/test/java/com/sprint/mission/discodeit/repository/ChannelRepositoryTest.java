package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AppConfig.class)
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("가시성 채널 조회 성공: 공개 채널과 사용자가 참여 중인 비공개 채널을 모두 조회할 수 있어야 합니다.")
  void findAllVisibleChannelsWithParticipants_success() {
    // given
    // 1. 사용자 생성 및 저장
    User user = userRepository.save(new User("test", "test@email.com", "test1234"));

    // 2. 공개 채널 생성 및 저장 (참여 여부와 상관없이 조회되어야 함)
    channelRepository.save(new Channel("Public Channel", "누구나 접근 가능한 공개 채널", ChannelType.PUBLIC));

    // 3. 참여 중인 비공개 채널 생성 및 저장 (참여 중이므로 조회되어야 함)
    Channel privateJoined = new Channel("Private Joined Channel", "참여 중인 비공개 채널",
        ChannelType.PRIVATE);
    // 연관 관계 편의 메서드를 통해 사용자를 채널에 참여
    privateJoined.addReadStatus(new ReadStatus(user, privateJoined, true));
    channelRepository.save(privateJoined);

    // 4. 참여하지 않은 비공개 채널 생성 및 저장 (참여하지 않았으므로 조회되면 안 됨)
    channelRepository.save(
        new Channel("Private Hidden Channel", "참여하지 않은 비공개 채널", ChannelType.PRIVATE));

    // when
    // 저장된 사용자의 ID를 기반으로 가시성 있는 채널 목록 조회
    List<Channel> channels = channelRepository.findAllVisibleChannelsWithParticipants(user.getId());

    // then
    // 공개 채널 1개와 참여 중인 비공개 채널 1개, 총 2개가 조회되어야 함
    assertThat(channels).hasSize(2);
    assertThat(channels).extracting(Channel::getName)
        .containsExactlyInAnyOrder("Public Channel", "Private Joined Channel")
        .doesNotContain("Private Hidden Channel");
  }

  @Test
  @DisplayName("상세 조회 실패: 존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다..")
  void findWithDetailById_fail() {
    // given
    UUID randomId = UUID.randomUUID();

    // when
    Optional<Channel> found = channelRepository.findById(randomId);

    // then
    assertThat(found).isEmpty();
  }
}
