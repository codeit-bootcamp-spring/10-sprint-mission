package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.dto.message.LastMessageTimeDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Tag("unit")
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private EntityManager em;

  private UUID c1Id;
  private UUID c2Id;
  private UUID c3Id;
  private UUID u1Id;
  private UUID u2Id;
  private UUID m1Id;
  private UUID m2Id;
  private UUID m3Id;
  private UUID m4Id;
  private UUID m5Id;

  @BeforeEach
  void setUp() {
    messageRepository.deleteAll();
    channelRepository.deleteAll();
    userRepository.deleteAll();

    //user setting
    userRepository.deleteAll();
    BinaryContent profile1 = BinaryContent.create("profile1", "jpg", 50);
    User user1 = User.create("test", "test@test.com", "test123", profile1);
    User user2 = User.create("test2", "test2@test.com", "test123", null);
    userRepository.saveAll(List.of(user1, user2));
    u1Id = user1.getId();
    u2Id = user2.getId();
    //channel setting
    Channel c1 = Channel.create(ChannelType.PUBLIC, "p1", "p1");
    Channel c2 = Channel.create(ChannelType.PRIVATE, null, null);
    Channel c3 = Channel.create(ChannelType.PRIVATE, null, null);
    channelRepository.saveAll(List.of(c1, c2, c3));
    c1Id = c1.getId();
    c2Id = c2.getId();
    c3Id = c3.getId();

    //message setting
    BinaryContent attachment1 = BinaryContent.create("attachment1", "jpg", 50);
    BinaryContent attachment2 = BinaryContent.create("attachment2", "jpg", 50);
    BinaryContent attachment3 = BinaryContent.create("attachment3", "jpg", 50);
    Message m1 = Message.create("m1", c1, user1, List.of(attachment1, attachment2));
    Message m2 = Message.create("m2", c1, user2, null);
    Message m3 = Message.create("m3", c1, user1, List.of(attachment3));
    Message m4 = Message.create("m4", c2, user1, null);
    Message m5 = Message.create("m5", c2, user2, null);
    ReflectionTestUtils.setField(m1, "createdAt", Instant.now().minusSeconds(50));
    ReflectionTestUtils.setField(m2, "createdAt", Instant.now().minusSeconds(40));
    ReflectionTestUtils.setField(m3, "createdAt", Instant.now().minusSeconds(30));
    ReflectionTestUtils.setField(m4, "createdAt", Instant.now().minusSeconds(20));
    ReflectionTestUtils.setField(m5, "createdAt", Instant.now().minusSeconds(10));
    messageRepository.saveAll(List.of(m1, m2, m3, m4, m5));
    m1Id = m1.getId();
    m2Id = m2.getId();
    m3Id = m3.getId();
    m4Id = m4.getId();
    m5Id = m5.getId();
  }

  @Test
  @DisplayName("성공: 채널 아이디로 페이지네이션이 적용된 메세지 목록을 조회(다음 페이지가 없음)")
  void findAllByChannelIdFetchUserInfoWithOutNext() {
    //@Query("select m from Message m left join fetch m.author left join fetch m.author.profile "
    //      + "left join fetch m.author.userStatus where m.channel.id = :channelId "
    //      + "and (cast(:cursor as timestamp) is null or m.createdAt < :cursor)")
    // given
    Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
    Instant cursor = Instant.now(); //모든 메시지

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdFetchUserInfo(
        c1Id, pageable, cursor
    );
    em.flush();
    em.clear();

    // then
    assertFalse(result.isEmpty());
    assertEquals(3, result.getContent().size());
    assertFalse(result.hasNext());
    //패치 조인 확인
    Message firstMessage = result.getContent().get(0);
    assertTrue(org.hibernate.Hibernate.isInitialized(firstMessage.getAuthor()),
        "Author가 로딩");
  }

  @Test
  @DisplayName("성공: 채널 아이디로 페이지네이션이 적용된 메세지 목록을 조회(다음 페이지가 있음)")
  void findAllByChannelIdFetchUserInfoWithNext() {
    //@Query("select m from Message m left join fetch m.author left join fetch m.author.profile "
    //      + "left join fetch m.author.userStatus where m.channel.id = :channelId "
    //      + "and (cast(:cursor as timestamp) is null or m.createdAt < :cursor)")
    // given
    Pageable pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "createdAt");
    Instant cursor = Instant.now(); //최신

    // when
    Slice<Message> result = messageRepository.findAllByChannelIdFetchUserInfo(
        c1Id, pageable, cursor
    );

    // then
    assertFalse(result.isEmpty());
    assertEquals(2, result.getContent().size());
    assertTrue(result.hasNext());//다음 메세지 있음
    assertNotNull(result.getContent().get(0).getAuthor().getUsername()); // 페치 조인 확인
  }

  @Test
  @DisplayName("성공: 여러 메세지를 첨부파일과 함께 조회")
  void findAllByIdInFetchAttachmentsSuccess() {
    //@Query("select m from Message m left join fetch m.attachments where m.id in :ids")
    //when
    List<Message> result = messageRepository.findAllByIdInFetchAttachments(List.of(m1Id, m2Id));

    assertEquals(2, result.size());
    assertEquals(2,
        result.stream().filter(m -> m.getId().equals(m1Id)).findFirst().get().getAttachments()
            .size());
    assertTrue(
        result.stream().filter(m -> m.getId().equals(m2Id)).findFirst().get().getAttachments()
            .isEmpty());

  }

  @Test
  @DisplayName("성공: 채널 아이디로 마지막 메세지 찾기")
  void findLastMessageByChannelIdSuccess() {
    //@Query(value = "SELECT * FROM messages WHERE channel_id = :channelId ORDER BY created_at DESC LIMIT 1",
    //      nativeQuery = true)
    //when
    Optional<Message> result = messageRepository.findLastMessageByChannelId(c1Id);

    //then
    assertTrue(result.isPresent());
    assertEquals(m3Id, result.get().getId());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 채널 아이디로 마지막 메시지를 조회 시, 빈 Optional 반환")
  void findLastMessageByWrongChannelId() {
    //given
    UUID wrongId = UUID.randomUUID();

    //when
    Optional<Message> result = messageRepository.findLastMessageByChannelId(wrongId);

    //then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("성공: 채널 아이디로 각 채널의 마지막 메세지 시간 찾기")
  void findAllLastMessagesByChannelIdSuccess() {
    //@Query(
    //      "SELECT new com.sprint.mission.discodeit.dto.message.LastMessageTimeDto(m.channel.id, MAX(m.createdAt)) "
    //          + "FROM Message m "
    //          + "WHERE m.channel.id IN :channelIds "
    //          + "GROUP BY m.channel.id")

    //when
    List<LastMessageTimeDto> result = messageRepository.findAllLastMessagesByChannelId(
        Set.of(c1Id, c2Id, c3Id));//c3는 메세지가 없음

    //then
    assertEquals(2, result.size());

  }

  @Test
  @DisplayName("실패: 빈 아이디 목록으로 마지막 메세지 시간 조회 시 빈 리스트 반환")
  void findAllLastMessagesByEmptyIdSet() {
    //when
    List<LastMessageTimeDto> result = messageRepository.findAllLastMessagesByChannelId(Set.of());

    //then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("성공: 채널아이디로 메세지 전부 삭제")
  void bulkDeleteByChannelIdSuccess() {
    //@Query("DELETE FROM Message m WHERE m.channel.id = :channelId")
    //when
    messageRepository.bulkDeleteByChannelId(c1Id);
    em.flush();
    em.clear();

    //then
    assertFalse(messageRepository.findById(m1Id).isPresent());
    assertFalse(messageRepository.findById(m2Id).isPresent());
    assertFalse(messageRepository.findById(m3Id).isPresent());
  }
}