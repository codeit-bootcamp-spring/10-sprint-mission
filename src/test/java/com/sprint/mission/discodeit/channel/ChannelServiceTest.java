package com.sprint.mission.discodeit.channel;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {
    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    @Test
    void createPublicChannelPassTest() {
        // given
        Channel channel = new Channel(
                ChannelType.PUBLIC,
                "공용 채널 통과",
                "테스트 통과한 공용 채널입니다."
        );
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                channel.getName(),
                channel.getDescription()
        );

        // when
        channelService.create(request);

        // then
        verify(channelService, times(1))
                .create(request);
    }

    @Test
    void createPublicChannelFailTest() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "공용 채널 생성 실패",
                "생성 실패한 공용 채널입니다."
        );

        // when
        channelService.create(request);

        // then
        verify(channelService, times(1))
                .create(request);
    }

    @Test
    void createPrivateChannelPassTest() {
        // given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                new ArrayList<UUID>()
        );

        // when
        channelService.create(request);

        // then
        verify(channelService, times(1))
                .create(request);
    }

    @Test
    void createPrivateChannelFailTest() {
        // given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                new ArrayList<UUID>()
        );

        // when
        channelService.create(request);

        // then
        verify(channelService, times(1))
                .create(request);
    }

    @Test
    void updateChannelPassTest() {
        // given
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
                new ArrayList<UUID>()
        );

        // when
        channelService.create(request);

        // then
        verify(channelService, times(1))
                .create(request);
    }

    @Test
    void updateChannelFailTest() {
        // given

        // when

        // then
    }

    @Test
    void deleteChannelPassTest() {
        // given

        // when

        // then
    }

    @Test
    void deleteChannelFailTest() {
        // given

        // when

        // then
    }

    @Test
    void findByUserIdPassTest() {
        // given

        // when

        // then
    }

    @Test
    void findByUserIdFailTest() {
        // given

        // when

        // then
    }
}
