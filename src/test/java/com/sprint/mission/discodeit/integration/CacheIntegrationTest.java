package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.CreateMessageRequestDto;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.CreateUserRequestDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class CacheIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CacheManager cacheManager;

    // 컨텍스트 전체를 띄울 때 AdminInitializer 같은 시작 로직이 진짜 리포지토리 동작을 필요로 하기 때문에 spy사용
    @MockitoSpyBean
    private UserRepository userRepository;
    @MockitoSpyBean
    private ChannelRepository channelRepository;
    @MockitoSpyBean
    private NotificationRepository notificationRepository;

    @BeforeEach
    void resetCachesAndInvocations() {
        cacheManager.getCacheNames()
                .forEach(name -> cacheManager.getCache(name).clear());
        clearInvocations(userRepository, channelRepository, notificationRepository);
    }

    @Test
    @DisplayName("users: 같은 조회를 두 번 호출하면 캐시 적중으로 DB 조회는 1회만 발생한다.")
    void usersCache_hit() {
        // given
        userService.findAll();

        // when
        userService.findAll();

        // then
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("users: 사용자 생성 시 캐시가 무효화되어 다음 조회에서 DB를 다시 조회한다.")
    void usersCache_evictedOnCreate() {
        // given
        userService.findAll();

        // when
        userService.create(new CreateUserRequestDto(
                "cache-" + UUID.randomUUID(), UUID.randomUUID() + "@test.com", "password123", null));
        userService.findAll();

        // then
        verify(userRepository, times(2)).findAll();
    }

    @Test
    @DisplayName("channels: 같은 사용자 채널 목록을 두 번 조회하면 DB 조회는 1회만 발생한다.")
    void channelsCache_hit() {
        // given
        UUID userId = UUID.randomUUID();
        channelService.findAllByUserId(userId);

        // when
        channelService.findAllByUserId(userId);

        // then
        verify(channelRepository, times(1)).findAccessibleChannelsByUserId(userId);
    }

    @Test
    @DisplayName("channels: 채널 생성 시 캐시가 무효화되어 다음 조회에서 DB를 다시 조회한다.")
    void channelsCache_evictedOnCreate() {
        // given
        UUID userId = UUID.randomUUID();
        channelService.findAllByUserId(userId);

        // when
        channelService.createPrivate(new CreatePrivateChannelRequestDto(null));
        channelService.findAllByUserId(userId);

        // then
        verify(channelRepository, times(2)).findAccessibleChannelsByUserId(userId);
    }

    @Test
    @DisplayName("notifications: 같은 수신자 알림 목록을 두 번 조회하면 저장소 조회는 1회만 발생한다.")
    void notificationsCache_hit() {
        // given
        UUID receiverId = UUID.randomUUID();
        notificationService.findAllByReceiverId(receiverId);

        // when
        notificationService.findAllByReceiverId(receiverId);

        // then
        verify(notificationRepository, times(1)).findAllByReceiverIdOrderByCreatedAtAsc(receiverId);
    }

    @Test
    @DisplayName("notifications: 알림 생성 시 해당 수신자 캐시가 무효화되어 다음 조회에서 다시 조회한다.")
    void notificationsCache_evictedOnCreate() {
        // given: receiver_id FK 충족을 위해 실제 사용자 생성
        UUID receiverId = userService.create(new CreateUserRequestDto(
                "notif-" + UUID.randomUUID(), UUID.randomUUID() + "@test.com", "password123", null)).id();
        notificationService.findAllByReceiverId(receiverId);

        // when
        notificationService.create(receiverId, "t", "c");
        notificationService.findAllByReceiverId(receiverId);

        // then
        verify(notificationRepository, times(2)).findAllByReceiverIdOrderByCreatedAtAsc(receiverId);
    }

    @Test
    @DisplayName("channels: 메시지 생성 시 채널 캐시가 무효화되어(lastMessageAt 변경) 다음 조회에서 다시 조회한다.")
    void channelsCache_evictedOnMessageCreate() {
        // given: 실제 작성자/채널 준비 (이 호출들도 channels 캐시를 비우므로 적재보다 먼저 수행)
        UserDto author = userService.create(new CreateUserRequestDto(
                "msg-" + UUID.randomUUID(), UUID.randomUUID() + "@test.com", "password123", null));
        ChannelDto channel = channelService.createPrivate(
                new CreatePrivateChannelRequestDto(null));
        UUID viewerId = UUID.randomUUID();
        channelService.findAllByUserId(viewerId);   // 캐시 적재

        // when
        messageService.create(
                new CreateMessageRequestDto("hi", channel.id(), author.id(), null), null);
        channelService.findAllByUserId(viewerId);

        // then
        verify(channelRepository, times(2)).findAccessibleChannelsByUserId(viewerId);
    }

    @Test
    @DisplayName("channels: 메시지 삭제 시 채널 캐시가 무효화되어 다음 조회에서 다시 조회한다.")
    void channelsCache_evictedOnMessageDelete() {
        // given
        UserDto author = userService.create(new CreateUserRequestDto(
                "msg-" + UUID.randomUUID(), UUID.randomUUID() + "@test.com", "password123", null));
        ChannelDto channel = channelService.createPrivate(
                new CreatePrivateChannelRequestDto(null));
        MessageDto message = messageService.create(
                new CreateMessageRequestDto("hi", channel.id(), author.id(), null), null);
        UUID viewerId = UUID.randomUUID();
        channelService.findAllByUserId(viewerId);   // 적재 (생성 무효화 이후)

        // when
        messageService.delete(message.id());
        channelService.findAllByUserId(viewerId);

        // then
        verify(channelRepository, times(2)).findAccessibleChannelsByUserId(viewerId);
    }
}
