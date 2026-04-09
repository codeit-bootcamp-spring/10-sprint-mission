package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("채널에서 가장 최근 메시지 불러오기 - 성공")
    void findTopByChannelIdOrderByCreatedAtDesc_success() {
        // given
        // alice라는 user 객체 생성 및 영속화
        User author = userRepository.save(new User("alice", "alice@test.com", "pw", null));
        // general 이름의 공용 채널 생성 및 영속화
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "desc"));

        // 메시지를 생성 및 DB에 적용
        saveMessageWithCreatedAt("first", channel, author,
            Instant.parse("2026-01-01T00:00:00Z")); // 첫 번쨰로 발행된 메시지
        saveMessageWithCreatedAt("second", channel, author,
            Instant.parse("2026-01-01T00:00:10Z")); // 두 번째로 발행된 메시지 (최근)

        // when
        // 채널 Id를 통해 가장 최근에 발행된 메시지를 반환하는 메서드 호출
        Message latest = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channel.getId());

        // then
        assertThat(latest).isNotNull(); // 결과물이 null이 아니면 검증 통과
        assertThat(latest.getContent()).isEqualTo("second"); // "second" 가 가장 최근 메시지임.
    }

    @Test
    @DisplayName("채널에서 가장 최근 메시지 불러오기 - 실패 (메시지 존재 X)")
    void findTopByChannelIdOrderByCreatedAtDesc_fail_whenNoMessage() {
        // given
        // general 채널 객체 생성 및 영속화
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "desc"));

        // when
        // 아무 메시지도 없는 general 채널에서 최근 메시지를 추출
        Message latest = messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channel.getId());

        // then
        // 결과는 당연히 null
        assertThat(latest).isNull();
    }

    @Test
    @DisplayName("채널 ID 리스트를 받고 채널 당 최근 메세지를 리스트로 반환 - 성공")
    void findLastMessageByChannelIds_success() {
        // when
        // alice 유저 객체 생성 및 영속화
        User author = userRepository.save(new User("alice", "alice@test.com", "pw", null));
        // 공용 채널 c1, c2 생성 및 영속화
        Channel c1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "c1", "desc"));
        Channel c2 = channelRepository.save(new Channel(ChannelType.PUBLIC, "c2", "desc"));

        // c1과 c2 채널에서 메시지 발행 (c1-latest, c2-latest)가 가장 최신 메시지
        saveMessageWithCreatedAt("c1-old", c1, author, Instant.parse("2026-01-01T00:00:00Z"));
        saveMessageWithCreatedAt("c1-latest", c1, author, Instant.parse("2026-01-01T00:00:30Z"));
        saveMessageWithCreatedAt("c2-latest", c2, author, Instant.parse("2026-01-01T00:00:20Z"));

        // when
        // c1, c2 id 리스트를 바탕으로 채널 별 최신 메시지를 리스트로 반환하는 메서드 호출
        List<Message> result = messageRepository.findLastMessageByChannelIds(
            List.of(c1.getId(), c2.getId()));

        // then
        // 결과 리스트의 사이즈가 2 -> 검증 통과
        assertThat(result).hasSize(2);
        // 결과 리스트의 이름이 "c1-latest, c2-latest"를 포함하면 검증 통과
        assertThat(result)
            .extracting(Message::getContent)
            .containsExactlyInAnyOrder("c1-latest", "c2-latest");
    }

    @Test
    @DisplayName("채널 ID 리스트를 받고 채널 당 최근 메세지를 리스트로 반환 - 실패 (메시지 존재 X)")
    void findLastMessageByChannelIds_fail_whenNoMessage() {
        // when
        // c1, c2 채널 객체 생성 및 영속화
        Channel c1 = channelRepository.save(new Channel(ChannelType.PUBLIC, "c1", "desc"));
        Channel c2 = channelRepository.save(new Channel(ChannelType.PUBLIC, "c2", "desc"));

        // when
        // c1, c2 id 리스트를 통해 호출
        List<Message> result = messageRepository.findLastMessageByChannelIds(
            List.of(c1.getId(), c2.getId()));

        // then
        // c1, c2는 메시지가 없으므로 결과도 empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 아이디를 통해 메시기 찾기 및 페이징 - 성공")
    void findByChannelId_withPageable_success() {
        // when
        // alice 유저 생성 및 영속화 & general 공용 채널 생성 및 영속화
        User author = userRepository.save(new User("alice", "alice@test.com", "pw", null));
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "desc"));

        // 메시지 발행
        saveMessageWithCreatedAt("old", channel, author, Instant.parse("2026-01-01T00:00:00Z"));
        saveMessageWithCreatedAt("mid", channel, author, Instant.parse("2026-01-01T00:00:10Z"));
        saveMessageWithCreatedAt("new", channel, author, Instant.parse("2026-01-01T00:00:20Z"));

        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        // pageable 객체를 바탕으로 페이징 된 general 채널의 결과를 slice 형태로 반환
        Slice<Message> slice = messageRepository.findByChannelId(channel.getId(), pageable);

        // then
        assertThat(slice.getContent()).hasSize(2); // pageable의 사이즈는 2 -> 검증 통과
        assertThat(slice.getContent().get(0).getContent()).isEqualTo(
            "new"); // pageable의 정렬 기준 : createdAt, Desc
        assertThat(slice.hasNext()).isTrue(); // 요소가 세개인데 사이즈는 두개 -> next 존재함
    }

    @Test
    @DisplayName("채널 아이디를 통해 메시기 찾기 및 페이징 - 실패 (메시지 존재 X)")
    void findByChannelId_withPageable_fail_whenEmpty() {
        // given
        // 채널만 생성 및 영속화
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "desc"));
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        // pageable 기준으로 채널에서 메시지 리스트를 페이징
        Slice<Message> slice = messageRepository.findByChannelId(channel.getId(), pageable);

        // then
        assertThat(slice.getContent()).isEmpty(); // 메시지가 없으므로 content는 비어있음.
        assertThat(slice.hasNext()).isFalse(); // 메시지가 없으므로 next 존재 X
    }

    @Test
    @DisplayName("채널 ID와 커서 기반으로 메시지 목록 찾기 - 성공")
    void findByChannelIdAndCursor_success() {
        // given
        // 유저와 채널 생성
        User author = userRepository.save(new User("alice", "alice@test.com", "pw", null));
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "desc"));

        // 해당 채널에 유저가 메시지를 작성함
        saveMessageWithCreatedAt("old", channel, author, Instant.parse("2026-01-01T00:00:00Z"));
        saveMessageWithCreatedAt("mid", channel, author, Instant.parse("2026-01-01T00:00:10Z"));
        saveMessageWithCreatedAt("new", channel, author, Instant.parse("2026-01-01T00:00:20Z"));

        // pageable 객체 생성 (사이즈는 2, 생성 시간 내림차순 정렬)
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        // channel 에서 pageable 객체 기준으로 지정 커서 (new) 보다 오래된 메시지들을 반환
        Slice<Message> slice = messageRepository.findByChannelIdAndCursor(
            channel.getId(), Instant.parse("2026-01-01T00:00:20Z"), pageable);

        // then
        assertThat(slice.getContent()).hasSize(2); // slice의 size = 2 (검증 ok)
        assertThat(slice.getContent())
            .extracting(Message::getContent)
            .containsExactly("mid", "old"); // cursor(new) 보다 오래된 두개의 메시지(mid old) 존재
    }

    @Test
    @DisplayName("채널 ID와 커서 기반으로 메시지 목록 찾기 - 실패 (커서 다음 값이 없음)")
    void findByChannelIdAndCursor_fail_whenNoOlderMessage() {
        // given
        // 유저와 채널 생성 및 영속화
        User author = userRepository.save(new User("alice", "alice@test.com", "pw", null));
        Channel channel = channelRepository.save(
            new Channel(ChannelType.PUBLIC, "general", "desc"));

        // 유일한 메시지 생성
        saveMessageWithCreatedAt("only", channel, author, Instant.parse("2026-01-01T00:00:20Z"));

        // pageable 객체 생성
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        // 채널에서 only 메세지 커서와 pageable 기반으로 메시지 찾기
        Slice<Message> slice = messageRepository.findByChannelIdAndCursor(
            channel.getId(), Instant.parse("2026-01-01T00:00:00Z"), pageable);

        // then
        // 커서 다음의 컨텐츠는 없으므로 Empty
        assertThat(slice.getContent()).isEmpty();
    }

    // 유틸 메서드
    // 메세지를 생성하고 영속화하는 기능
    private void saveMessageWithCreatedAt(String content, Channel channel, User author,
        Instant createdAt) {
        Message message = new Message(content, channel, author, List.of());
        Message saved = messageRepository.save(message);
        entityManager.flush(); // DB에 반영
        entityManager.createNativeQuery(
                "update messages set created_at = :createdAt where id = :id")
            .setParameter("createdAt", createdAt)
            .setParameter("id", saved.getId())
            .executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }
}
