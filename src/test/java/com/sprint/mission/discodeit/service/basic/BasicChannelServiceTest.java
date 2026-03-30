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
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("곽인성 채널","잡담");
        Channel mockChannel = mock(Channel.class);
        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "곽인성 채널", "잡담", null, null);
        when(channelRepository.save(any(Channel.class))).thenReturn(mockChannel);
        when(channelMapper.toDto(any(Channel.class))).thenReturn(channelDto);
        // when
        ChannelDto createdChannel = channelService.create(request); //channelDto 값이 createdChannel로 들어간다.
        // then
        assertNotNull(createdChannel);
        assertEquals(request.name(),createdChannel.name());
        assertEquals(request.description(),createdChannel.description());
    }

@Test
@DisplayName("PRIVATE 채널 생성이 진행되어야한다.")
void create_private_channel() {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId1, userId2));

        ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, null, null);
        when(channelRepository.save(any(Channel.class))).thenReturn(null);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(userRepository.findAllById(request.participantIds())).thenReturn(List.of(user1, user2));
        when(readStatusRepository.saveAll(anyList())).thenReturn(List.of());
        when(channelMapper.toDto(any(Channel.class))).thenReturn(channelDto); //이게 createdChannel의 값


        // when
        ChannelDto createdChannel = channelService.create(request);

        // then
        assertNotNull(createdChannel);
        assertEquals(ChannelType.PRIVATE,createdChannel.type());
        verify(channelRepository, times(1)).save(any(Channel.class));
        verify(readStatusRepository, times(1)).saveAll(anyList());

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

        when(channel1.getId()).thenReturn(UUID.randomUUID());
        when(channel2.getId()).thenReturn(UUID.randomUUID());
        when(readStatus1.getChannel()).thenReturn(channel1);
        when(readStatus2.getChannel()).thenReturn(channel2);

        when(readStatusRepository.findAllByUserId(userId1)).thenReturn(List.of(readStatus1, readStatus2)); //ReadStatus List
        when(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList())).thenReturn(List.of(channel1, channel2));//Channel List
        when(channelMapper.toDto(any(Channel.class))).thenReturn(mock(ChannelDto.class));

        // when
        List<ChannelDto> createdChannels = channelService.findAllByUserId(userId1);


        // then
        assertNotNull(createdChannels);
        assertEquals(2,createdChannels.size());
        verify(readStatusRepository).findAllByUserId(userId1);
        verify(channelRepository).findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), anyList());

    }

    @Test
    @DisplayName("PUBLIC채널 수정이 진행되어야한다.")
    void update_public_channel() {
        // given
        Channel channel = mock(Channel.class);
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("new인성채널", "new소통");
        ChannelDto result = new ChannelDto(channelId, ChannelType.PUBLIC, request.newName(), request.newDescription(), null, null);
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(channel.getType()).thenReturn(ChannelType.PUBLIC);
        when(channelMapper.toDto(any(Channel.class))).thenReturn(result);

        // when
        ChannelDto updatedChannel = channelService.update(channelId, request);

        // then
        assertNotNull(updatedChannel);
        assertEquals(ChannelType.PUBLIC,updatedChannel.type());
        assertEquals(request.newName(), updatedChannel.name());
        assertEquals(request.newDescription(), updatedChannel.description());

    }

    @Test
    @DisplayName("수정할 채널 타입이 PRIVATE라면 수정에 실패해야한다.")
    void update_fail_when_channel_type_private() {

        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("인성", "소통");
        Channel channel = mock(Channel.class);
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(channel.getType()).thenReturn(ChannelType.PRIVATE);

        // when, then
        assertThrows(PrivateChannelUpdateException.class, () -> channelService.update(channelId, request));
        verify(channelRepository, never()).save(any(Channel.class));
        verify(channel, never()).update(anyString(), anyString());
    }

    @Test
    @DisplayName("채널 삭제가 진행되어야한다.")
    void delete_channel_success() {
        // given
        when(channelRepository.existsById(any(UUID.class))).thenReturn(true);

        // when
        channelService.delete(UUID.randomUUID());

        // then
        verify(channelRepository, times(1)).deleteById(any(UUID.class));
        verify(messageRepository, times(1)).deleteAllByChannelId(any(UUID.class));
        verify(readStatusRepository, times(1)).deleteAllByChannelId(any(UUID.class));

    }

    @Test
    @DisplayName("존재하지 않는 채널을 삭제할경우 예외가 발생한다.")
    void delete_fail_when_not_exist_channel() {
        // given
        when(channelRepository.existsById(any(UUID.class))).thenReturn(false);

        // when, then
        assertThrows(ChannelNotFoundException.class, () -> channelService.delete(UUID.randomUUID()));
        verify(channelRepository, never()).deleteById(any(UUID.class));
        verify(messageRepository, never()).deleteAllByChannelId(any(UUID.class));
        verify(readStatusRepository, never()).deleteAllByChannelId(any(UUID.class));
    }
}