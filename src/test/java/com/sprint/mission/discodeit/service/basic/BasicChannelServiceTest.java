package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.response.ApiException;
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

            // given
            CreatePublicChannelRequest request =
                    new CreatePublicChannelRequest("공지", "공지 채널");

            // when
            UUID channelId = channelService.createPublicChannel(request);

            // then
            Channel channel = channelRepository.findById(channelId).orElseThrow();

            assertThat(channel.getName()).isEqualTo("공지");
            assertThat(channel.getDescription()).isEqualTo("공지 채널");
            assertThat(channel.getMemberIds()).isEmpty();
            assertThat(channel.isPublic()).isTrue();
        }

        @Test
        @DisplayName("PUBLIC 채널 조회 응답 타입 검증")
        void findPublicChannel_responseType() {

            // given
            UUID channelId = channelService.createPublicChannel(
                    new CreatePublicChannelRequest("공지", "공지 채널")
            );

            // when
            ChannelResponse response =
                    channelService.findChannelByChannelId(channelId);

            // then
            assertThat(response).isInstanceOf(PublicChannelResponse.class);

            PublicChannelResponse publicResponse =
                    (PublicChannelResponse) response;

            assertThat(publicResponse.name()).isEqualTo("공지");
            assertThat(publicResponse.description()).isEqualTo("공지 채널");
            assertThat(publicResponse.channelType()).isEqualTo(ChannelType.PUBLIC);
            assertThat(publicResponse.lastMessageAt()).isNull();
        }
    }

    @Nested
    @DisplayName("PRIVATE 채널")
    class PrivateChannelTest {

        @Test
        @DisplayName("PRIVATE 채널 생성 성공")
        void createPrivateChannel_success() {

            // given
            CreatePrivateChannelRequest request =
                    new CreatePrivateChannelRequest(
                            Set.of(userId1, userId2)
                    );

            // when
            UUID channelId = channelService.createPrivateChannel(request);

            // then
            Channel channel = channelRepository.findById(channelId).orElseThrow();

            assertThat(channel.isPrivate()).isTrue();
            assertThat(channel.getMemberIds())
                    .containsExactlyInAnyOrder(userId1, userId2);

            List<ReadStatus> readStatuses =
                    readStatusRepository.findAllByChannelId(channelId);

            assertThat(readStatuses).hasSize(2);
        }

        @Test
        @DisplayName("PRIVATE 채널 조회 응답 타입 검증")
        void findPrivateChannel_responseType() {

            // given
            UUID channelId = channelService.createPrivateChannel(
                    new CreatePrivateChannelRequest(
                            Set.of(userId1, userId2)
                    )
            );

            // when
            ChannelResponse response =
                    channelService.findChannelByChannelId(channelId);

            // then
            assertThat(response).isInstanceOf(PrivateChannelResponse.class);

            PrivateChannelResponse privateResponse =
                    (PrivateChannelResponse) response;

            assertThat(privateResponse.memberIds())
                    .containsExactlyInAnyOrder(userId1, userId2);

            assertThat(privateResponse.channelType())
                    .isEqualTo(ChannelType.PRIVATE);

            assertThat(privateResponse.lastMessageAt()).isNull();
        }

        @Test
        @DisplayName("PRIVATE 채널 정보 수정 시 예외 발생")
        void updatePrivateChannel_fail() {

            // given
            UUID channelId = channelService.createPrivateChannel(
                    new CreatePrivateChannelRequest(Set.of(userId1))
            );

            UpdateChannelRequest request =
                    new UpdateChannelRequest(
                            channelId,
                            "수정",
                            "설명"
                    );

            // then
            assertThatThrownBy(() -> channelService.updateChannelInfo(request))
                    .isInstanceOf(ApiException.class);
        }
    }

    @Test
    @DisplayName("유저별 Visible 채널 조회")
    void findVisibleChannel_success() {

        UUID publicChannelId =
                channelService.createPublicChannel(
                        new CreatePublicChannelRequest("공개", "desc")
                );

        UUID privateChannelId =
                channelService.createPrivateChannel(
                        new CreatePrivateChannelRequest(Set.of(userId1))
                );

        List<ChannelResponse> visible =
                channelService.findAllChannelsByUserId(userId1);

        assertThat(visible)
                .extracting(ChannelResponse::id)
                .contains(publicChannelId, privateChannelId);
    }

    @Test
    @DisplayName("채널 마지막 메시지 시간 채널의 최신 메시지 기준으로 반환")
    void findChannel_lastMessageAt_success() {
        // given
        UUID channelId =
                channelService.createPublicChannel(
                        new CreatePublicChannelRequest("공지", "공지 채널")
                );

        Message first = new Message(userId1, channelId, "첫번째");
        messageRepository.save(first);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Message second = new Message(userId1, channelId, "두번째");
        messageRepository.save(second);

        // when
        ChannelResponse response =
                channelService.findChannelByChannelId(channelId);

        // then
        assertThat(response.lastMessageAt())
                .isEqualTo(second.getCreatedAt());
    }

    @Test
    @DisplayName("Private 멤버 아닌 경우 조회 불가")
    void privateChannel_notVisible() {
        channelService.createPrivateChannel(
                new CreatePrivateChannelRequest(Set.of(userId1))
        );

        List<ChannelResponse> visible =
                channelService.findAllChannelsByUserId(userId2);

        assertThat(visible).isEmpty();
    }

    @Test
    @DisplayName("채널 삭제 성공")
    void deleteChannel_success() {
        UUID channelId =
                channelService.createPrivateChannel(
                        new CreatePrivateChannelRequest(Set.of(userId1))
                );

        channelService.deleteChannel(channelId);

        assertThat(channelRepository.findById(channelId)).isEmpty();
        assertThat(readStatusRepository.findAllByChannelId(channelId)).isEmpty();
    }
}
