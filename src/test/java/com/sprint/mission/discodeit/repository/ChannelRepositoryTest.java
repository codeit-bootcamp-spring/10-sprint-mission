package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  @DisplayName("채널 이름으로 존재 여부 확인 성공")
  void existsByName_Success() {
    // given
    Channel channel = new Channel("backend-team", ChannelType.PUBLIC);
    entityManager.persist(channel);
    entityManager.flush();

    // when
    boolean result = channelRepository.existsByName("backend-team");

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("채널 이름으로 존재 여부 확인 실패 - 존재하지 않는 이름")
  void existsByName_Fail() {
    // when
    boolean result = channelRepository.existsByName("non-existent-channel");

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("채널 목록 페이징 및 이름 오름차순 정렬 성공")
  void findAll_PageAndSort_Success() {
    // given
    entityManager.persist(new Channel("a-room", ChannelType.PUBLIC));
    entityManager.persist(new Channel("b-room", ChannelType.PUBLIC));
    entityManager.persist(new Channel("c-room", ChannelType.PUBLIC));
    entityManager.flush();

    // when
    Page<Channel> page = channelRepository.findAll(
        PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"))
    );

    // then
    assertThat(page.getContent()).extracting(Channel::getName)
        .containsExactly("a-room", "b-room");
    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.hasNext()).isTrue();
  }

  @Test
  @DisplayName("채널 목록 페이징 실패 - 범위를 벗어난 페이지는 빈 결과")
  void findAll_PageAndSort_Fail_EmptyPage() {
    // given
    entityManager.persist(new Channel("a-room", ChannelType.PUBLIC));
    entityManager.flush();

    // when
    Page<Channel> page = channelRepository.findAll(
        PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "name"))
    );

    // then
    assertThat(page.getContent()).isEmpty();
    assertThat(page.getTotalElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("ID로 채널 조회 성공")
  void findById_Success() {
    // given
    Channel channel = new Channel("general-room", ChannelType.PUBLIC);
    entityManager.persist(channel);
    entityManager.flush();

    // when
    Channel foundChannel = channelRepository.findById(channel.getId()).orElse(null);

    // then
    assertThat(foundChannel).isNotNull();
    assertThat(foundChannel.getName()).isEqualTo("general-room");
  }

  @Test
  @DisplayName("ID로 채널 조회 실패 - 존재하지 않는 ID")
  void findById_Fail() {
    // when
    Channel foundChannel = channelRepository.findById(UUID.randomUUID()).orElse(null);

    // then
    assertThat(foundChannel).isNull();
  }
}
