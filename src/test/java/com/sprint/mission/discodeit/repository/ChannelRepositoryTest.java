package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Tag("unit")
class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("성공: 공개 채널만 조회 성공")
  void findAllPublic() {
    //@Query("SELECT c FROM Channel c WHERE c.type = 'PUBLIC'")
    //given
    Channel c1 = Channel.create(ChannelType.PUBLIC, "p1", "p1");
    Channel c2 = Channel.create(ChannelType.PUBLIC, "p2", "p2");
    Channel c3 = Channel.create(ChannelType.PUBLIC, "p3", "p3");
    Channel c4 = Channel.create(ChannelType.PRIVATE, null, null);
    Channel c5 = Channel.create(ChannelType.PRIVATE, null, null);
    channelRepository.saveAll(List.of(c1, c2, c3, c4, c5));

    //when
    List<Channel> publicChannels = channelRepository.findAllPublic();

    //then
    assertEquals(3, publicChannels.size());
  }
}