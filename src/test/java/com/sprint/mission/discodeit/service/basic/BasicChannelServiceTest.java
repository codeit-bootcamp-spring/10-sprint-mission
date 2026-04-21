package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.Inherited;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class BasicChannelServiceTest {
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicChannelService basicChannelService;

    // CREATE CHANNEL
    @Test
    void create_public_succecss() {
        // given
        PublicChannelCreateRequest request =
                new PublicChannelCreateRequest("general", "desc");
        Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
        given(channelRepository.save(any(Channel.class)))
                .willReturn(channel);

        // when
        Channel result = basicChannelService.create(request);

        // then
        assertEquals(ChannelType.PUBLIC, result.getType());
        assertEquals("general", result.getName());

        then(channelRepository).should().save(any(Channel.class));
    }

    // PRIVATE Channel CREATE
    @Test
    void create_private_success() {
        // given
        UUID userId = UUID.randomUUID();

        PrivateChannelCreateRequest request =
                new PrivateChannelCreateRequest(List.of(userId));

        Channel channel = new Channel(ChannelType.PRIVATE, "대화방", "");
        User user = mock(User.class);

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        Channel result = basicChannelService.create(request);

        // then
        assertEquals(ChannelType.PRIVATE, result.getType());
        then(readStatusRepository).should().saveAll(anyList());
    }
    @Test
    void create_private_fail_user_not_found() {
        // given
        UUID userId = UUID.randomUUID();

        PrivateChannelCreateRequest request =
                new PrivateChannelCreateRequest(List.of(userId));

        given(channelRepository.save(any(Channel.class)))
                .willReturn(new Channel(ChannelType.PRIVATE, "", ""));

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> basicChannelService.create(request));
    }

    // UPDATE
    @Test
    void update_success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "old", "old");

        PublicChannelUpdateRequest request =
                new PublicChannelUpdateRequest("new", "newDesc");

        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(channel));

        // when
        Channel updated = basicChannelService.update(channelId, request);

        // then
        assertEquals("new", updated.getName());
        assertEquals("newDesc", updated.getDescription());

        then(channelRepository).should().findById(channelId);
    }
    @Test
    void update_fail_not_found() {
        // given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.findById(channelId))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(ChannelNotFoundException.class,
                () -> basicChannelService.update(channelId,
                        new PublicChannelUpdateRequest("new", "desc")));
    }

    @Test
    void update_fail_private_channel() {
        // given
        UUID channelId = UUID.randomUUID();

        Channel channel = new Channel(ChannelType.PRIVATE, "private", "desc");

        given(channelRepository.findById(channelId))
                .willReturn(Optional.of(channel));

        // when & then
        assertThrows(PrivateChannelUpdateException.class,
                () -> basicChannelService.update(channelId,
                        new PublicChannelUpdateRequest("new", "desc")));
    }

    @Test
    void delete_fail_not_found() {
        // given
        UUID channelId = UUID.randomUUID();

        given(channelRepository.findById(channelId))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(ChannelNotFoundException.class,
                () -> basicChannelService.delete(channelId));
    }

    // FIND-BY-USER
    @Test
    void findAllByUser_success() {
        // given
        UUID userId = UUID.randomUUID();

        Channel publicChannel = new Channel(ChannelType.PUBLIC, "pub", "d");
        Channel privateChannel = new Channel(ChannelType.PRIVATE, "pri", "d");

        ReadStatus readStatus = mock(ReadStatus.class);
        given(readStatus.getChannel()).willReturn(privateChannel);

        given(readStatusRepository.findAllByUser_Id(userId))
                .willReturn(List.of(readStatus));

        given(channelRepository.findAll())
                .willReturn(List.of(publicChannel, privateChannel));

        // when
        List<Channel> result = basicChannelService.findAllByUser_Id(userId);

        // then
        assertEquals(2, result.size());
    }




}
