package com.sprint.mission.discodeit.channel;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {
    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    @Test
    void createPublicChannelPassTest() {
        // given

        // when

        // then
    }

    @Test
    void createPublicChannelFailTest() {
        // given

        // when

        // then
    }

    @Test
    void createPrivateChannelPassTest() {
        // given

        // when

        // then
    }

    @Test
    void createPrivateChannelFailTest() {
        // given

        // when

        // then
    }

    @Test
    void updateChannelPassTest() {
        // given

        // when

        // then
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
