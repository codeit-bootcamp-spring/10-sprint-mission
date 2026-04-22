package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequestDto;
import com.sprint.mission.discodeit.dto.UpdateChannelRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private ChannelMapper channelMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private BasicChannelService basicChannelService;

    @Test
    @DisplayName("Public 채널을 유효한 데이터로 생성합니다")
    void create_public_success() {
        // given
        CreatePublicChannelRequestDto request = new CreatePublicChannelRequestDto("testChannel",
            "test description");
        Channel channel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        ChannelDto expectedDto = new ChannelDto(
            channel.getId(), channel.getName(), channel.getDescription(), channel.getType(),
            Instant.now(), List.of());

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

        // when
        ChannelDto result = basicChannelService.createPublic(request);

        // then
        assertEquals(expectedDto, result);

        verify(channelRepository).save(any(Channel.class));
    }

    @Test
    @DisplayName("Private 채널을 성공적으로 생성하고 참여자를 추가합니다.")
    void create_private_success() {
        // given
        UUID participantId = UUID.randomUUID();
        CreatePrivateChannelRequestDto request = new CreatePrivateChannelRequestDto(
            List.of(participantId));

        Channel channel = new Channel(ChannelType.PRIVATE, "testChannel",
            "test private description");
        User participant = new User("testParticipant", "test@test.com", "password");
        ChannelDto expectedDto = new ChannelDto(
            UUID.randomUUID(), channel.getName(), channel.getDescription(), channel.getType(),
            Instant.now(), List.of());

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(participantId)).willReturn(Optional.of(participant));
        given(channelMapper.toDto(any(Channel.class))).willReturn(expectedDto);

        // when
        ChannelDto result = basicChannelService.createPrivate(request);

        // then
        assertEquals(expectedDto, result);
        verify(channelRepository).save(any(Channel.class));
        verify(readStatusRepository).save(any(ReadStatus.class));
        ;
    }

    @Test
    @DisplayName("존재하지 않는 유저를 참여자로 지정하면 Private 채널 생성을 할 수 없습니다.")
    void create_private_fail_userNotFound() {
        // given
        UUID notFoundUserId = UUID.randomUUID();
        CreatePrivateChannelRequestDto request = new CreatePrivateChannelRequestDto(
            List.of(notFoundUserId));
        Channel channel = new Channel(ChannelType.PRIVATE, "testChannel",
            "test private description");

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(notFoundUserId)).willThrow(
            new UserNotFoundException(notFoundUserId));

        // when, then
        assertThrows(UserNotFoundException.class, () -> basicChannelService.createPrivate(request));
    }


    @Test
    @DisplayName("특정 유저가 참여 가능한 채널 목록을 성공적으로 조회합니다.")
    void find_all_by_user_id_success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        Channel channel = new Channel(ChannelType.PUBLIC, "testChannel", "test description");
        // channel 객체에 id 주입
        ReflectionTestUtils.setField(channel, "id", channelId);

        ChannelDto expectedDto = new ChannelDto(
            channel.getId(), channel.getName(), channel.getDescription(), channel.getType(),
            Instant.now(), List.of());

        given(channelRepository.findAccessibleChannelsByUserId(userId)).willReturn(
            List.of(channel));

        given(readStatusRepository.findAllByChannelIdIn(anyList())).willReturn(List.of());
        given(messageRepository.findLastMessageTimesByChannelIds(anyList())).willReturn(List.of());
        given(channelMapper.toDtoWithContext(any(), any(), any())).willReturn(expectedDto);

        // when
        List<ChannelDto> result = basicChannelService.findAllByUserId(userId);

        // then
        assertEquals(1, result.size());
        assertEquals(expectedDto, result.get(0));

    }

    @Test
    @DisplayName("유저가 접근 가능한 채널이 없을 경우 빈 리스트를 반환합니다.")
    void findAllByUserId_emptyList() {
        // given
        UUID userId = UUID.randomUUID();
        given(channelRepository.findAccessibleChannelsByUserId(userId)).willReturn(List.of());

        // when
        List<ChannelDto> result = basicChannelService.findAllByUserId(userId);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Public 채널의 정보를 수정합니다.")
    void update_success() {
        // given
        UUID channelId = UUID.randomUUID();
        UpdateChannelRequestDto request = new UpdateChannelRequestDto("newName", "newDescription");
        Channel channel = new Channel(ChannelType.PUBLIC, "oldName", "oldDescription");
        ChannelDto expectedDto = new ChannelDto(channelId, request.newName(),
            request.newDescription(), channel.getType(), Instant.now(), List.of());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(channel)).willReturn(expectedDto);

        // when
        ChannelDto result = basicChannelService.update(channelId, request);

        // then
        assertEquals("newName", channel.getName());
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("존재하지 않는 채널을 수정할 수 없습니다.")
    void update_fail_channelNotFound() {
        // given
        UUID notFoundChannelId = UUID.randomUUID();
        UpdateChannelRequestDto request = new UpdateChannelRequestDto("newName", "newDescription");

        given(channelRepository.findById(notFoundChannelId)).willReturn(Optional.empty());

        // when, then
        assertThrows(ChannelNotFoundException.class,
            () -> basicChannelService.update(notFoundChannelId, request));
    }

    @Test
    @DisplayName("채널을 연관 데이터 포함하여 삭제합니다.")
    void delete_success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "testChannel", "test description");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

        // when
        basicChannelService.delete(channelId);

        // then
        verify(channelRepository).deleteById(channelId);
        verify(readStatusRepository).deleteAllByChannelId(channelId);
        verify(messageRepository).deleteAllByChannelId(channelId);

    }

    @Test
    @DisplayName("존재하지 않는 채널은 삭제할 수 없습니다.")
    void delete_fail_not_found_channel() {
        // given
        UUID notFoundChannelId = UUID.randomUUID();

        given(channelRepository.findById(notFoundChannelId)).willReturn(Optional.empty());

        // when, then
        assertThrows(ChannelNotFoundException.class,
            () -> basicChannelService.delete(notFoundChannelId));
    }

    @Test
    @DisplayName("Private 채널을 수정하려고 시도하면 PrivateChannelUpdateException 예외가 발생한다.")
    void update_private_channel_exception() {
        // given: 타입이 PRIVATE인 채널을 준비
        UUID channelId = UUID.randomUUID();
        UpdateChannelRequestDto request = new UpdateChannelRequestDto("newName", "newDescription");
        Channel privateChannel = new Channel(ChannelType.PRIVATE, "oldName", "oldDescription");

        given(channelRepository.findById(channelId)).willReturn(Optional.of(privateChannel));

        // when & then: Private 채널은 이름/설명 수정이 불가하므로 예외 발생
        assertThrows(
            PrivateChannelUpdateException.class,
            () -> basicChannelService.update(channelId, request));
    }

    @Test
    @DisplayName("존재하지 않는 채널 ID로 단일 조회 시 ChannelNotFoundException 예외가 발생한다.")
    void find_channel_not_found_exception() {
        // given
        UUID notFoundChannelId = UUID.randomUUID();
        given(channelRepository.findById(notFoundChannelId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ChannelNotFoundException.class, () -> basicChannelService.find(notFoundChannelId));
    }
}