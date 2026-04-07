package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private BasicChannelService channelService;

    @Test
    @DisplayName("PUBLIC 채널 생성이 진행되어야한다.")
    void create_public_channel() {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("곽인성 채널", "잡담");
        Channel mockChannel = mock(Channel.class);
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "곽인성 채널", "잡담", null, null);
        given(channelRepository.save(any(Channel.class))).willReturn(mockChannel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto createdChannel = channelService.create(request);

        // then
        assertNotNull(createdChannel);
        assertEquals(request.name(), createdChannel.name());
        assertEquals(request.description(), createdChannel.description());
        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("PRIVATE 채널 생성이 진행되어야한다.")
    void create_private_channel() {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId1, userId2));
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, null, null);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        given(channelRepository.save(any(Channel.class))).willReturn(null);
        given(userRepository.findAllById(request.participantIds())).willReturn(List.of(user1, user2));
        given(readStatusRepository.saveAll(anyList())).willReturn(List.of());
        given(channelMapper.toDto(any(Channel.class))).willReturn(channelDto);

        // when
        ChannelDto createdChannel = channelService.create(request);

        // then
        assertNotNull(createdChannel);
        assertEquals(ChannelType.PRIVATE, createdChannel.type());
        then(channelRepository).should(times(1)).save(any(Channel.class));
        then(readStatusRepository).should(times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("유저가 참여한 채널 리스트를 조회해야한다.")
    void get_channel_list_by_user_id() {
        // given
        UUID userId1 = UUID.randomUUID();
        Channel channel1 = mock(Channel.class);
        Channel channel2 = mock(Channel.class);
        ReadStatus readStatus1 = mock(ReadStatus.class);
        ReadStatus readStatus2 = mock(ReadStatus.class);

        given(channel1.getId()).willReturn(UUID.randomUUID());
        given(channel2.getId()).willReturn(UUID.randomUUID());
        given(readStatus1.getChannel()).willReturn(channel1);
        given(readStatus2.getChannel()).willReturn(channel2);
        given(readStatusRepository.findAllByUserId(userId1)).willReturn(List.of(readStatus1, readStatus2));
        given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList())).willReturn(List.of(channel1, channel2));
        given(channelMapper.toDto(any(Channel.class))).willReturn(mock(ChannelDto.class));

        // when
        List<ChannelDto> createdChannels = channelService.findAllByUserId(userId1);

        // then
        assertNotNull(createdChannels);
        assertEquals(2, createdChannels.size());
        then(readStatusRepository).should().findAllByUserId(userId1);
        then(channelRepository).should().findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());
    }

    @Test
    @DisplayName("PUBLIC채널 수정이 진행되어야한다.")
    void update_public_channel() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = mock(Channel.class);
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new인성채널", "new소통");
        ChannelDto result = new ChannelDto(channelId, ChannelType.PUBLIC, request.newName(), request.newDescription(), null, null);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channel.getType()).willReturn(ChannelType.PUBLIC);
        given(channelMapper.toDto(any(Channel.class))).willReturn(result);

        // when
        ChannelDto updatedChannel = channelService.update(channelId, request);

        // then
        assertNotNull(updatedChannel);
        assertEquals(ChannelType.PUBLIC, updatedChannel.type());
        assertEquals(request.newName(), updatedChannel.name());
        assertEquals(request.newDescription(), updatedChannel.description());
        then(channel).should().update("new인성채널", "new소통");
    }

    @Test
    @DisplayName("수정할 채널 타입이 PRIVATE라면 수정에 실패해야한다.")
    void update_fail_when_channel_type_private() {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("인성", "소통");
        Channel channel = mock(Channel.class);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channel.getType()).willReturn(ChannelType.PRIVATE);

        // when, then
        assertThrows(PrivateChannelUpdateException.class, () -> channelService.update(channelId, request));
        then(channelRepository).should(never()).save(any(Channel.class));
        then(channel).should(never()).update(anyString(), anyString());
    }

    @Test
    @DisplayName("채널 삭제가 진행되어야한다.")
    void delete_channel_success() {
        // given
        given(channelRepository.existsById(any(UUID.class))).willReturn(true);

        // when
        channelService.delete(UUID.randomUUID());

        // then
        then(channelRepository).should(times(1)).deleteById(any(UUID.class));
        then(messageRepository).should(times(1)).deleteAllByChannelId(any(UUID.class));
        then(readStatusRepository).should(times(1)).deleteAllByChannelId(any(UUID.class));
    }

    @Test
    @DisplayName("존재하지 않는 채널을 삭제할경우 예외가 발생한다.")
    void delete_fail_when_not_exist_channel() {
        // given
        given(channelRepository.existsById(any(UUID.class))).willReturn(false);

        // when, then
        assertThrows(ChannelNotFoundException.class, () -> channelService.delete(UUID.randomUUID()));
        then(channelRepository).should(never()).deleteById(any(UUID.class));
        then(messageRepository).should(never()).deleteAllByChannelId(any(UUID.class));
        then(readStatusRepository).should(never()).deleteAllByChannelId(any(UUID.class));
    }
}