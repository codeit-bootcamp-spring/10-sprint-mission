package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.TestJpaAuditConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestJpaAuditConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("공개 채널이 정상적으로 등록됩니다.")
    void save_public_channel() {
        // given
        Channel channel = new Channel("channel", "description", ChannelType.PUBLIC);

        // when
        Channel savedChannel = channelRepository.save(channel);

        // then
        assertThat(savedChannel.getId()).isNotNull();
        assertThat(channelRepository.existsByName("channel")).isTrue();
    }

    @Test
    @DisplayName("비공개 채널이 정상적으로 등록됩니다.")
    void save_private_channel() {
        // given
        Channel channel = new Channel(null, null, ChannelType.PRIVATE);

        // when
        Channel savedChannel = channelRepository.save(channel);

        // then
        assertThat(savedChannel.getType()).isEqualTo(ChannelType.PRIVATE);
        assertThat(savedChannel.getName()).isNull();
        assertThat(savedChannel.getDescription()).isNull();
    }

    @Test
    @DisplayName("채널 이름이 겹치면 true를 반환합니다.")
    void if_channel_name_already_exists_return_true() {
        // given
        Channel channel = new Channel("channel", "description", ChannelType.PUBLIC);
        channelRepository.save(channel);

        // when
        boolean result = channelRepository.existsByName("channel");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("채널 이름이 겹치지 않으면 false를 반환합니다.")
    void if_channel_name_does_not_exists_return_false() {
        // when
        boolean result = channelRepository.existsByName("channel");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("채널 타입으로 전체 채널 조회가 가능합니다.")
    void find_by_channel_type() {
        // given
        Channel channel1 = new Channel("channel1", "description", ChannelType.PUBLIC);
        Channel channel2 = new Channel("channel2", "description", ChannelType.PUBLIC);
        Channel channel3 = new Channel(null, null, ChannelType.PRIVATE);
        channelRepository.saveAll(List.of(channel1, channel2, channel3));

        // when
        List<Channel> channels = channelRepository.findAllByType(ChannelType.PUBLIC);

        // then
        assertThat(channels).hasSize(2);

        assertThat(channels)
                .allMatch(c -> c.getType() == ChannelType.PUBLIC);
        assertThat(channels)
                .extracting(Channel::getName)
                .containsExactlyInAnyOrder("channel1", "channel2");
    }

    @Test
    @DisplayName("해당 타입의 채널이 없으면 빈 리스트를 반환합니다.")
    void find_by_channel_type_empty() {
        // given
        Channel channel = new Channel("channel", "desc", ChannelType.PUBLIC);
        channelRepository.save(channel);

        // when
        List<Channel> result = channelRepository.findAllByType(ChannelType.PRIVATE);

        // then
        assertThat(result).isEmpty();
    }
}