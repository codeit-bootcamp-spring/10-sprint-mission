package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class BasicReadStatusServiceTest {

    @Autowired
    private BasicReadStatusService readStatusService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("ReadStatus 생성 성공")
    void testCreateReadStatus() {
        User user = new User("user1", "1234", "user1@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("test", "desc");
        channelRepository.save(channel);

        Instant lastReadAt = Instant.now();
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(user.getId(), channel.getId(),
                lastReadAt);

        UUID readStatusId = readStatusService.createReadStatus(request).id();
        flushAndClear();

        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow();
        assertThat(readStatus.getUser().getId()).isEqualTo(user.getId());
        assertThat(readStatus.getChannel().getId()).isEqualTo(channel.getId());
        assertThat(readStatus.getLastReadAt()).isEqualTo(lastReadAt);
    }

    @Test
    @DisplayName("같은 userId + channelId 조합이면 생성 실패")
    void testDuplicateReadStatusFail_sameUserAndChannel() {
        User user = new User("user6", "1234", "user6@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("c6", "d6");
        channelRepository.save(channel);

        ReadStatusCreateRequest request = new ReadStatusCreateRequest(
                user.getId(),
                channel.getId(),
                Instant.now()
        );
        readStatusService.createReadStatus(request);

        assertThatThrownBy(() -> readStatusService.createReadStatus(request))
                .isInstanceOf(DiscodeitException.class);
    }

    @Test
    @DisplayName("ReadStatus 단건 조회 성공")
    void testFindReadStatusById() {
        User user = new User("user2", "1234", "user2@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("test2", "desc2");
        channelRepository.save(channel);

        ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
        readStatusRepository.save(readStatus);
        flushAndClear();

        ReadStatusDto response = readStatusService.findReadStatusByReadStatusId(readStatus.getId());

        assertThat(response.id()).isEqualTo(readStatus.getId());
        assertThat(response.userId()).isEqualTo(user.getId());
        assertThat(response.channelId()).isEqualTo(channel.getId());
    }

    @Test
    @DisplayName("User 기준 ReadStatus 전체 조회")
    void testFindAllReadStatusesByUserId() {
        User user = new User("user3", "1234", "user3@test.com");
        userRepository.save(user);

        Channel ch1 = Channel.buildPublic("c1", "d1");
        Channel ch2 = Channel.buildPublic("c2", "d2");
        channelRepository.save(ch1);
        channelRepository.save(ch2);

        readStatusRepository.save(new ReadStatus(user, ch1, Instant.now()));
        readStatusRepository.save(new ReadStatus(user, ch2, Instant.now()));
        flushAndClear();

        List<ReadStatusDto> responses = readStatusService.findAllReadStatusesByUserId(user.getId());
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("ReadStatus 수정 성공")
    void testUpdateReadStatus() {
        User user = new User("user4", "1234", "user4@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("c", "d");
        channelRepository.save(channel);

        ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
        readStatusRepository.save(readStatus);

        Instant newLastReadAt = Instant.now();
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newLastReadAt);

        ReadStatusDto response = readStatusService.updateReadStatus(readStatus.getId(), request);
        flushAndClear();

        assertThat(response.id()).isEqualTo(readStatus.getId());
        assertThat(response.lastReadAt()).isEqualTo(newLastReadAt);
    }

    @Test
    @DisplayName("ReadStatus 삭제 성공")
    void testDeleteReadStatus() {
        User user = new User("user5", "1234", "user5@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("c5", "d5");
        channelRepository.save(channel);

        ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());
        readStatusRepository.save(readStatus);

        readStatusService.deleteReadStatus(readStatus.getId());
        flushAndClear();

        assertThat(readStatusRepository.findById(readStatus.getId())).isEmpty();
    }

    @Test
    @DisplayName("채널 참여자 초기 ReadStatus 생성 성공")
    void createInitialReadStatuses_success() {
        User user1 = new User("init-user1", "1234", "init1@test.com");
        User user2 = new User("init-user2", "1234", "init2@test.com");
        userRepository.save(user1);
        userRepository.save(user2);

        Channel channel = Channel.buildPrivate();
        channelRepository.save(channel);

        Instant lastReadAt = Instant.now();
        readStatusService.createInitialReadStatuses(
                channel.getId(),
                Set.of(user1.getId(), user2.getId()),
                lastReadAt
        );
        flushAndClear();

        List<ReadStatus> readStatuses = readStatusRepository.findByChannelIn(List.of(channel));
        assertThat(readStatuses).hasSize(2);
        assertThat(readStatuses)
                .extracting(rs -> rs.getUser().getId())
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    @DisplayName("ReadStatus 생성 시 User, Channel 양방향 연관관계 일관성 유지")
    void readStatus_bidirectionalConsistency() {
        User user = new User("user-bidir", "1234", "user-bidir@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("bidir", "desc");
        channelRepository.save(channel);

        ReadStatus readStatus = new ReadStatus(user, channel, Instant.now());

        assertThat(readStatus.getUser()).isEqualTo(user);
        assertThat(readStatus.getChannel()).isEqualTo(channel);
        assertThat(user.getReadStatuses()).contains(readStatus);
        assertThat(channel.getReadStatuses()).contains(readStatus);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
