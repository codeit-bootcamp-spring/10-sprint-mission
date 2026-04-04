package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class ChannelIntegrationTest {

    @Autowired
    ChannelService channelService;

    @Autowired
    UserService userService;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    ReadStatusRepository readStatusRepository;

    @Test
    @DisplayName("공용 채널 생성 동작")
    @Transactional
    void channel_create_public_fully_works() {
        // given
        PublicChannelCreateDTO req = new PublicChannelCreateDTO("general", "desc");

        // when
        ChannelDto saved = channelService.createPublicChannel(req);

        // then
        Channel channel = channelRepository.findById(saved.id()).orElseThrow();
        assertThat(channel.getName()).isEqualTo(req.name());
        assertThat(channel.getDescription()).isEqualTo(req.description());
        assertThat(saved.participants()).isEmpty();
    }

    @Test
    @DisplayName("사설 채널 생성 동작")
    @Transactional
    void channel_create_private_fully_works() {
        // given
        UserDto u1 = userService.create(
            new UserCreateRequestDTO("user1", "u1" + "@test.com", "pw"), null);
        UserDto u2 = userService.create(
            new UserCreateRequestDTO("user2", "u2" + "@test.com", "pw"), null);

        // when + then
        // 유저들 participants 기반으로 사설 채널 생성
        PrivateChannelCreateDTO req = new PrivateChannelCreateDTO(List.of(u1.id(), u2.id()));
        ChannelDto saved = channelService.createPrivateChannel(req);

        assertThat(saved.id()).isNotNull(); // 사설 채널이 생성되었는지 검증 (id not null)
        assertThat(channelRepository.findById(saved.id())).isPresent();
        assertThat(readStatusRepository.findAllByChannelId(saved.id())).hasSize(
            2); // 사설 채널의 유저의 명수가 2명인지 검증
    }

    @Test
    @DisplayName("채널 수정 동작")
    @Transactional
    void channel_update_fully_works() {
        // given
        // 수정할 기존의 채널
        ChannelDto created = channelService.createPublicChannel(
            new PublicChannelCreateDTO("general", "desc"));

        // 수정 요청 dto
        PublicChannelUpdateRequestDTO req = new PublicChannelUpdateRequestDTO(
            "general-new", "desc-new");

        // when
        ChannelDto updated = channelService.update(created.id(), req);

        // then
        Channel channel = channelRepository.findById(created.id()).orElseThrow();
        assertThat(updated.name()).isEqualTo(req.newName()); // 업데이트된 채널의 이름이 수정 요청 dto의 이름과 동일한가?
        assertThat(updated.description()).isEqualTo(req.newDescription());
        assertThat(channel.getName()).isEqualTo(req.newName());
        assertThat(channel.getDescription()).isEqualTo(req.newDescription());
    }

    @Test
    @DisplayName("채널 삭제 동작")
    @Transactional
    void channel_delete_fully_works() {
        // given
        ChannelDto created = channelService.createPublicChannel(
            new PublicChannelCreateDTO("general", "desc"));

        // when
        channelService.delete(created.id());

        // then
        assertThat(channelRepository.findById(created.id())).isEmpty();
        assertThat(readStatusRepository.findAllByChannelId(created.id())).isEmpty();
    }
}
