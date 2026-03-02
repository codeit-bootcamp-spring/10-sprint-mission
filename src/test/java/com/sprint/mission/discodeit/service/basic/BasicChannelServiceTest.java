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
import com.sprint.mission.discodeit.utils.FileIOHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
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

    UUID userId1;
    UUID userId2;

    @BeforeEach
    void setUp() {
        FileIOHelper.flushData();

        User user1 = new User("user1", "1234", "u1@test.com");
        userRepository.save(user1);
        userId1 = user1.getId();

        User user2 = new User("user2", "1234", "u2@test.com");
        userRepository.save(user2);
        userId2 = user2.getId();
    }

    @Nested
    @DisplayName("PUBLIC 채널")
    class PublicChannelTest {
        @Test
        @DisplayName("PUBLIC 채널 생성 성공")
        void createPublicChannel_success() {
            PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지 채널");
            UUID channelId = channelService.createPublicChannel(request).getId();

            Channel channel = channelRepository.findById(channelId).orElseThrow();
            assertThat(channel.getName()).isEqualTo("공지");
            assertThat(channel.getDescription()).isEqualTo("공지 채널");
            assertThat(channel.getMemberIds()).isEmpty();
            assertThat(channel.isPublic()).isTrue();
        }
    }

    @Nested
    @DisplayName("PRIVATE 채널")
    class PrivateChannelTest {
        @Test
        @DisplayName("PRIVATE 채널 생성 성공")
        void createPrivateChannel_success() {
            PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(Set.of(userId1, userId2));
            UUID channelId = channelService.createPrivateChannel(request).getId();

            Channel channel = channelRepository.findById(channelId).orElseThrow();
            assertThat(channel.isPrivate()).isTrue();
            assertThat(channel.getMemberIds()).containsExactlyInAnyOrder(userId1, userId2);

//            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
//            assertThat(readStatuses).hasSize(2);
        }

        @Test
        @DisplayName("PRIVATE 채널 정보 수정 시 예외 발생")
        void updatePrivateChannel_fail() {
            UUID channelId = channelService.createPrivateChannel(
                    new PrivateChannelCreateRequest(Set.of(userId1))
            ).getId();

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
        ).getId();

        UUID privateChannelId = channelService.createPrivateChannel(
                new PrivateChannelCreateRequest(Set.of(userId1))
        ).getId();

        List<ChannelDto> visible = channelService.findAllChannelsByUserId(userId1);

        assertThat(visible)
                .extracting(ChannelDto::id)
                .contains(publicChannelId, privateChannelId);
    }

    @Test
    @DisplayName("채널 마지막 메시지 시간 채널의 최신 메시지 기준으로 반환")
    void findChannel_lastMessageAt_success() {
        UUID channelId = channelService.createPublicChannel(
                new PublicChannelCreateRequest("공지", "공지 채널")
        ).getId();

        Message first = new Message(userId1, channelId, "첫번째");
        messageRepository.save(first);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Message second = new Message(userId1, channelId, "두번째");
        messageRepository.save(second);

        ChannelDto response = channelService.findAllChannelsByUserId(userId1).stream()
                .filter(c -> c.id().equals(channelId))
                .findFirst()
                .orElseThrow();

        assertThat(response.lastMessageAt()).isEqualTo(second.getCreatedAt());
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
        UUID channelId = channelService.createPrivateChannel(
                new PrivateChannelCreateRequest(Set.of(userId1))
        ).getId();

        channelService.deleteChannel(channelId);

        assertThat(channelRepository.findById(channelId)).isEmpty();
//        assertThat(readStatusRepository.findAllByChannelId(channelId)).isEmpty();
    }
}
