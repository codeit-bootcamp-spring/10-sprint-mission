package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditConfig.class)
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  private User testUser;
  private Channel testChannel;

  @BeforeEach
  void setUp() {
    testUser = userRepository.save(new User("test", "test@email.com", "test1234"));
    testChannel = channelRepository.save(
        new Channel("공개 채널", "이 곳은 공개 채널 입니다", ChannelType.PUBLIC));
  }

  @Test
  @DisplayName("최신 메시지 조회 성공: 특정 채널의 가장 마지막 마시지를 가져와야 합니다.")
  void findFirstByChannelOrderByCreatedAtDesc_success() {
    // given
    messageRepository.save(new Message(testUser, testChannel, "첫번쨰 메시지", null));
    Message lastMessage = messageRepository.save(
        new Message(testUser, testChannel, "두번째 메시지", null));

    // when
    Optional<Message> found = messageRepository.findFirstByChannelOrderByCreatedAtDesc(testChannel);

    // then
    assertThat(found).isNotEmpty();
    assertThat(found.get().getId()).isEqualTo(lastMessage.getId());
    assertThat(found.get().getContent()).isEqualTo(lastMessage.getContent());
  }

  @Test
  @DisplayName("첫 페이지 조회 성공: 커서가 없을 때 특정 채널의 메시지를 최신순으로 조회할 수 있어야 합니다.")
  void findByChannelOrderByCreatedAtDesc_success() {
    // given
    messageRepository.save(new Message(testUser, testChannel, "메시지 1", null));
    messageRepository.save(new Message(testUser, testChannel, "메시지 2", null));
    messageRepository.save(new Message(testUser, testChannel, "메시지 3", null));

    PageRequest pageable = PageRequest.of(0, 2);

    // when
    List<Message> result = messageRepository.findByChannelOrderByCreatedAtDesc(testChannel,
        pageable);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getContent()).isEqualTo("메시지 3");
    assertThat(result.get(1).getContent()).isEqualTo("메시지 2");
  }

  @Test
  @DisplayName("페이징 조회 성공: 커서보다 이전 시간의 데이터가 없으면 빈 리스트를 반환합니다.")
  void findByChannelAndCursor_success() {
    // given
    Message oldest = messageRepository.save(new Message(testUser, testChannel, "가장 오래된 메시지", null));
    messageRepository.flush();

    Instant cursor = oldest.getCreatedAt();
    PageRequest pageable = PageRequest.of(0, 5);

    // when
    List<Message> result = messageRepository.findByChannelAndCursor(testChannel, cursor, pageable);

    // then
    assertThat(result).isEmpty();
  }
}