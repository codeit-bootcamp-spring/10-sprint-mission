package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.response.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class BasicChannelServiceTest {

    @Autowired
    BasicChannelService channelService;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReadStatusRepository readStatusRepository;

    @Autowired
    MessageRepository messageRepository;

    @PersistenceContext
    private EntityManager entityManager;

    UUID userId1;
    UUID userId2;

    @BeforeEach
    void setUp() {

        User user1 = new User("user1", "1234", "u1@test.com");
        userRepository.save(user1);
        userId1 = user1.getId();

        User user2 = new User("user2", "1234", "u2@test.com");
        userRepository.save(user2);
        userId2 = user2.getId();

        flushAndClear();
    }

    @Nested
    @DisplayName("PUBLIC 채널")
    class PublicChannelTest {

        @Test
        @DisplayName("PUBLIC 채널 생성 성공")
        void createPublicChannel_success() {
            PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지 채널");
            UUID channelId = channelService.createPublicChannel(request).id();
            flushAndClear();

            Channel channel = channelRepository.findById(channelId).orElseThrow();
            assertThat(channel.getName()).isEqualTo("공지");
            assertThat(channel.getDescription()).isEqualTo("공지 채널");
            assertThat(channel.getReadStatuses()).isEmpty();
            assertThat(channel.isPublic()).isTrue();
        }
    }

    @Nested
    @DisplayName("PRIVATE 채널")
    class PrivateChannelTest {

        @Test
        @DisplayName("PRIVATE 채널 생성 성공")
        void createPrivateChannel_success() {
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                    Set.of(userId1, userId2));
            UUID channelId = channelService.createPrivateChannel(request).id();
            flushAndClear();

            Channel channel = channelRepository.findById(channelId).orElseThrow();
            assertThat(channel.isPrivate()).isTrue();
            List<ReadStatus> readStatuses = readStatusRepository.findByChannelIn(List.of(channel));
            assertThat(readStatuses).hasSize(2);
            assertThat(readStatuses)
                    .extracting(readStatus -> readStatus.getUser().getId())
                    .containsExactlyInAnyOrder(userId1, userId2);

        }

        @Test
        @DisplayName("PRIVATE 채널 정보 수정 시 예외 발생")
        void updatePrivateChannel_fail() {
            UUID channelId = channelService.createPrivateChannel(
                    new PrivateChannelCreateRequest(Set.of(userId1))
            ).id();

            PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("수정", "설명");

            assertThatThrownBy(() -> channelService.updateChannelInfo(channelId, request))
                    .isInstanceOf(ApiException.class);
        }
    }

    @Test
    @DisplayName("유저별 Visible 채널 조회")
    void findVisibleChannel_success() {
        UUID publicChannelId = channelService.createPublicChannel(
                new PublicChannelCreateRequest("공개", "desc")
        ).id();

        UUID privateChannelId = channelService.createPrivateChannel(
                new PrivateChannelCreateRequest(Set.of(userId1))
        ).id();

        List<ChannelDto> visible = channelService.findAllChannelsByUserId(userId1);

        assertThat(visible)
                .extracting(ChannelDto::id)
                .contains(publicChannelId, privateChannelId);
    }

    @Test
    @DisplayName("채널 단건 조회 성공")
    void findChannelByChannelId_success() {
        UUID channelId = channelService.createPublicChannel(
                new PublicChannelCreateRequest("단건조회", "desc")
        ).id();

        ChannelDto response = channelService.findChannelByChannelId(channelId);

        assertThat(response.id()).isEqualTo(channelId);
        assertThat(response.name()).isEqualTo("단건조회");
    }

    @Test
    @DisplayName("PUBLIC 채널 정보 수정 성공")
    void updatePublicChannel_success() {
        UUID channelId = channelService.createPublicChannel(
                new PublicChannelCreateRequest("before", "before-desc")
        ).id();

        ChannelDto response = channelService.updateChannelInfo(
                channelId,
                new PublicChannelUpdateRequest("after", "after-desc")
        );
        flushAndClear();

        Channel channel = channelRepository.findById(channelId).orElseThrow();
        assertThat(response.name()).isEqualTo("after");
        assertThat(response.description()).isEqualTo("after-desc");
        assertThat(channel.getName()).isEqualTo("after");
        assertThat(channel.getDescription()).isEqualTo("after-desc");
    }

    @Test
    @DisplayName("채널 조회 시 lastMessageAt 은 최신 메시지 시간으로 반환")
    void findChannelByChannelId_returnsLatestLastMessageAt() {
        Channel channel = channelRepository.save(Channel.buildPublic("메시지채널", "desc"));
        User user = userRepository.findById(userId1).orElseThrow();

        messageRepository.save(new Message(user, channel, "first"));
        flushAndClear();

        ChannelDto response = channelService.findChannelByChannelId(channel.getId());
        Channel savedChannel = channelRepository.findById(channel.getId()).orElseThrow();

        assertThat(savedChannel.getLastMessageAt()).isNotNull();
        assertThat(response.lastMessageAt()).isEqualTo(savedChannel.getLastMessageAt());
    }

    @Test
    @DisplayName("Private 멤버 아닌 경우 조회 불가")
    void privateChannel_notVisible() {
        channelService.createPrivateChannel(new PrivateChannelCreateRequest(Set.of(userId1)));

        List<ChannelDto> visible = channelService.findAllChannelsByUserId(userId2);
        assertThat(visible).isEmpty();
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel_success() {
        UUID channelId = channelService.createPublicChannel(
                new PublicChannelCreateRequest("삭제용", "삭제 테스트")
        ).id();

        channelService.deleteChannel(channelId);
        assertThat(channelRepository.existsById(channelId)).isFalse();
    }

    @Test
    @DisplayName("채널 삭제 시 메시지와 읽음 상태 양방향 참조도 함께 정리")
    void deleteChannel_clearsBidirectionalReferences() {
        User user = userRepository.findById(userId1).orElseThrow();
        Channel channel = channelRepository.save(Channel.buildPrivate());

        ReadStatus readStatus = readStatusRepository.save(new ReadStatus(user, channel, java.time.Instant.now()));
        Message message = messageRepository.save(new Message(user, channel, "hello"));

        assertThat(channel.getReadStatuses()).contains(readStatus);
        assertThat(channel.getMessages()).contains(message);
        assertThat(user.getReadStatuses()).contains(readStatus);
        assertThat(user.getMessages()).contains(message);

        channelService.deleteChannel(channel.getId());

        assertThat(channel.getReadStatuses()).isEmpty();
        assertThat(channel.getMessages()).isEmpty();
        assertThat(user.getReadStatuses()).doesNotContain(readStatus);
        assertThat(user.getMessages()).doesNotContain(message);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
