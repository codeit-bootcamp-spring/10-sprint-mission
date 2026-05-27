package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("성공: 특정 ChannelType에 해당하는 채널 목록만 정확히 조회한다")
  void findByType_Success() {
    // given
    channelRepository.save(new Channel("공개방 1", "설명", ChannelType.PUBLIC));
    channelRepository.save(new Channel("공개방 2", "설명", ChannelType.PUBLIC));
    channelRepository.save(new Channel(null, null, ChannelType.PRIVATE));

    // when
    List<Channel> publicChannels = channelRepository.findByType(ChannelType.PUBLIC);
    List<Channel> privateChannels = channelRepository.findByType(ChannelType.PRIVATE);

    // then
    assertThat(publicChannels).hasSize(2);
    assertThat(publicChannels.get(0).getType()).isEqualTo(ChannelType.PUBLIC);

    assertThat(privateChannels).hasSize(1);
    assertThat(privateChannels.get(0).getType()).isEqualTo(ChannelType.PRIVATE);
  }

  @Test
  @DisplayName("실패: 해당하는 타입의 채널이 없으면 빈 리스트를 반환한다")
  void findByType_Empty() {
    // given - 아무 데이터도 넣지 않음

    // when
    List<Channel> results = channelRepository.findByType(ChannelType.PUBLIC);

    // then
    assertThat(results).isEmpty();
  }
}