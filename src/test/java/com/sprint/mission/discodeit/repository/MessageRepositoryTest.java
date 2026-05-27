package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.channel.ChannelLastMessageQueryDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("성공: 특정 시간 이전의 메시지들을 Slice로 페이징하여 가져온다")
  void findAllByChannelIdWithAuthor_Success() {
    // given
    User author = userRepository.save(new User("tester", "test@test.com", "pw", null));
    Channel channel = channelRepository.save(new Channel("방", "설명", ChannelType.PUBLIC));

    // 순서대로 저장 (Auditing에 의해 createdAt 자동 생성)
    messageRepository.save(new Message("메시지1", author, channel, null));
    messageRepository.save(new Message("메시지2", author, channel, null));

    // 시간을 미래로 설정하여 모든 메시지가 포함되도록 설정
    Instant futureTime = Instant.now().plusSeconds(100);
    PageRequest pageRequest = PageRequest.of(0, 10);

    // when
    Slice<Message> messages = messageRepository.findAllByChannelIdWithAuthor(channel.getId(),
        futureTime, pageRequest);

    // then
    assertThat(messages).isNotNull();
  }

  @Test
  @DisplayName("성공: 메시지 ID로 단건 조회 시 연관된 엔티티를 한 번에 가져온다")
  void findByIdWithDetails_Success() {
    // given
    User author = userRepository.save(new User("tester", "test@test.com", "pw", null));
    Channel channel = channelRepository.save(new Channel("상세 방", "설명", ChannelType.PUBLIC));
    Message savedMessage = messageRepository.save(new Message("상세조회 테스트", author, channel, null));

    // 진짜 쿼리가 나가는지 확인하기 위해 영속성 컨텍스트 비우기
    entityManager.flush();
    entityManager.clear();

    // when
    Optional<Message> foundMessage = messageRepository.findByIdWithDetails(savedMessage.getId());

    // then
    assertThat(foundMessage).isPresent();
    assertThat(foundMessage.get().getContent()).isEqualTo("상세조회 테스트");

    // Fetch Join으로 가져온 작성자 정보가 잘 들어있는지 확인
    assertThat(foundMessage.get().getAuthor().getUsername()).isEqualTo("tester");
  }

  @Test
  @DisplayName("성공: 특정 채널의 가장 최근 메시지 생성 시간(MAX createdAt)을 조회한다")
  void findLastMessageAtByChannelId_Success() {
    // given
    User author = userRepository.save(new User("tester", "test@test.com", "pw", null));
    Channel channel = channelRepository.save(new Channel("방", "설명", ChannelType.PUBLIC));

    messageRepository.save(new Message("옛날 메시지", author, channel, null));
    Message lastMessage = messageRepository.save(new Message("최근 메시지", author, channel, null));

    // when
    Optional<Instant> lastTime = messageRepository.findLastMessageAtByChannelId(channel.getId());

    // then
    assertThat(lastTime).isPresent();
    // 가장 마지막에 저장된 메시지의 시간과 일치하는지 확인
    assertThat(lastTime.get().toEpochMilli())
        .isEqualTo(lastMessage.getCreatedAt().toEpochMilli());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 채널 ID 목록으로 마지막 메시지 시간을 조회하면 빈 리스트를 반환한다")
  void findLastMessagesByChannelIds_Fail_Empty() {
    // given
    List<UUID> fakeChannelIds = List.of(UUID.randomUUID(), UUID.randomUUID());

    // when
    List<com.sprint.mission.discodeit.dto.channel.ChannelLastMessageQueryDto> results =
        messageRepository.findLastMessagesByChannelIds(fakeChannelIds);

    // then
    assertThat(results).isEmpty();
  }

  @Test
  @DisplayName("성공: 여러 채널 ID 목록을 받아 각 채널별 마지막 메시지 시간을 DTO로 매핑하여 반환한다")
  void findLastMessagesByChannelIds_Success() {
    // given
    User author = userRepository.save(new User("tester", "test@test.com", "pw", null));

    Channel channel1 = channelRepository.save(new Channel("방1", "설명1", ChannelType.PUBLIC));
    Channel channel2 = channelRepository.save(new Channel("방2", "설명2", ChannelType.PUBLIC));

    messageRepository.save(new Message("방1 - 안녕", author, channel1, null));
    messageRepository.save(new Message("방2 - 하이", author, channel2, null));
    Message lastMsgCh2 = messageRepository.save(new Message("방2 - 잘가", author, channel2, null));

    List<UUID> channelIds = List.of(channel1.getId(), channel2.getId());

    // when
    List<ChannelLastMessageQueryDto> results = messageRepository.findLastMessagesByChannelIds(
        channelIds);

    // then
    assertThat(results).hasSize(2);

    // Group By 결과가 DTO 객체로 정상 변환되었는지 검증
    boolean channel2Exists = results.stream()
        .anyMatch(dto -> dto.channelId().equals(channel2.getId())
            && dto.lastMessageAt().toEpochMilli() == lastMsgCh2.getCreatedAt().toEpochMilli());
    assertThat(channel2Exists).isTrue();
  }
}