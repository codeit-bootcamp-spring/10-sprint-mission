package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("ChannelRepositoy 슬리이스 테스트")
public class ChannelRepositoryTest {
    @Autowired
    private ChannelRepository channelRepository;

    @Nested
    @DisplayName("채널 저장 테스트")
    class SaveChannelTest{
        @Test
        @DisplayName("정상적인 PUBLIC 채널 정보를 저장하면 DB에 영속화된다.")
        void should_save_channel_when_valid_input() {
            // given
            Channel channel = new Channel(ChannelType.PUBLIC, "일반", "기본 채팅방");
            // when
            Channel savedChannel = channelRepository.save(channel);
            // then
            assertThat(savedChannel).isNotNull();
            assertThat(savedChannel.getId()).isNotNull();
            assertThat(savedChannel.getCreatedAt()).isNotNull();
            assertThat(savedChannel.getType()).isEqualTo(ChannelType.PUBLIC);
            assertThat(savedChannel.getName()).isEqualTo("일반");
            assertThat(savedChannel.getDescription()).isEqualTo("기본 채팅방");
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest{
        @Test
        @DisplayName("존재하는 채널 ID로 조회하면 채널을 반환한다")
        void should_returnChannel_when_idExists() {
            // given
            Channel saved = channelRepository.save(
                    new Channel(ChannelType.PUBLIC, "general", "기본 채널")
            );

            // when
            Optional<Channel> result = channelRepository.findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
            assertThat(result.get().getName()).isEqualTo("general");
            assertThat(result.get().getDescription()).isEqualTo("기본 채널");
        }

        @Test
        @DisplayName("존재하지 않는 채널 ID 조회하면 Empty 반환")
        void should_return_empty_when_id_not_exists(){
            // given , when
            // 그냥 던지기.
            Optional<Channel> result = channelRepository.findById(UUID.randomUUID());

            // then
            assertThat(result).isEmpty();

        }
    }
    @Nested
    @DisplayName("findAll 테스트")
    class FindAllTest {
        @Test
        @DisplayName("여러 채널을 저장하면 전체 채널 목록을 조회할 수 있다")
        void should_return_all_channels_when_channels_Saved() {
            // given
            channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "기본 채널"));
            channelRepository.save(new Channel(ChannelType.PRIVATE, "", ""));

            // when
            List<Channel> result = channelRepository.findAll();

            // then
            assertThat(result).hasSize(2);
        }
    }
    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("저장된 채널을 삭제하면 더 이상 조회되지 않는다")
        void should_deleteChannel_when_channelExists() {
            // given
            Channel saved = channelRepository.save(
                    new Channel(ChannelType.PUBLIC, "general", "기본 채널")
            );

            // when
            channelRepository.deleteById(saved.getId());

            // then
            Optional<Channel> result = channelRepository.findById(saved.getId());
            assertThat(result).isEmpty();
        }
    }


}
