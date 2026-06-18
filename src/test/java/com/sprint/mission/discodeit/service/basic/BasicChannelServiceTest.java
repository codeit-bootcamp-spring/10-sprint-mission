package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {
    @Mock
    ChannelRepository channelRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ReadStatusRepository readStatusRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    ChannelMapper channelMapper;
    @InjectMocks
    BasicChannelService channelService;

    // create
    @Test
    @DisplayName("공용 채널 생성시 성공하여 channelDto를 반환해야 한다")
    void create_public_channel_success() {
        // given
        String name = "코드잇";
        String description = "코드잇 채널입니다";
        ChannelType channelType = ChannelType.PUBLIC;
        UUID fakeId = UUID.randomUUID();
        Channel channel = new Channel(name, description);
        ReflectionTestUtils.setField(channel, "id", fakeId);

        PublicChannelCreateRequest request = new PublicChannelCreateRequest();
        request.setName(name);
        request.setDescription(description);

        ChannelDto expectDto = new ChannelDto(
                channel.getId(),
                request.getName(),
                request.getDescription(),
                null,
                Instant.now(),
                channelType
        );

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(channelMapper.toDto(any(Channel.class))).willReturn(expectDto);
        // when
        ChannelDto resultDto = channelService.createPublic(request);
        // then
        assertEquals(expectDto.getId(), resultDto.getId());
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertEquals(expectDto.getDescription(), resultDto.getDescription());
        assertThat(resultDto.getDescription()).isEqualTo(expectDto.getDescription());
        assertThat(resultDto.getType()).isEqualTo(expectDto.getType());
        assertThat(resultDto.getName()).isEqualTo(expectDto.getName());
        assertThat(resultDto.getLastMessageAt()).isEqualTo(expectDto.getLastMessageAt());
    }

    @Test
    @DisplayName("개인 채널 생성시 성공하여 channelDto를 반환해야 한다")
    void create_private_channel_success() {
        // given
        ChannelType channelType = ChannelType.PRIVATE;
        UUID fakeId = UUID.randomUUID();
        Channel channel = new Channel();
        User user = new User();
        UserDto userDto = new UserDto(
                fakeId,
                "김현재",
                "fred@naver.com",
                null,
                true
        );

        ReflectionTestUtils.setField(channel, "id", fakeId);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest();
        request.setParticipantIds(List.of(fakeId));

        ReadStatus readStatus = new ReadStatus(user, channel);

        ChannelDto expectDto = new ChannelDto(
                channel.getId(),
                null,
                null,
                List.of(userDto),
                Instant.now(),
                channelType
        );

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(user));
        given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);
        given(channelMapper.toDto(any(Channel.class))).willReturn(expectDto);
        // when
        ChannelDto resultDto = channelService.createPrivate(request);
        // then
        assertEquals(expectDto.getId(), resultDto.getId());
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getDescription()).isEqualTo(expectDto.getDescription());
        assertThat(resultDto.getType()).isEqualTo(expectDto.getType());
        assertThat(resultDto.getLastMessageAt()).isEqualTo(expectDto.getLastMessageAt());
    }

    @Test
    @DisplayName("개인 채널 생성시 참여자를 찾을 수 없어 user no found 예외 발생해야 한다")
    void create_private_channel_participants_not_found_fail() {
        // given
        UUID fakeId = UUID.randomUUID();
        Channel channel = new Channel();

        ReflectionTestUtils.setField(channel, "id", fakeId);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest();
        request.setParticipantIds(List.of(fakeId));

        given(channelRepository.save(any(Channel.class))).willReturn(channel);
        given(userRepository.findById(fakeId)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> channelService.createPrivate(request))
                .isInstanceOf(UserNotFoundException.class);
        then(readStatusRepository).should(never()).save(any(ReadStatus.class));
        then(channelMapper).should(never()).toDto(any(Channel.class));
    }
    // update
    @Test
    @DisplayName("공용 채널 수정시 성공하여 channelDto를 반환해야한다")
    void update_public_channel_success() {
        // given
        String name = "코드잇";
        String description = "코드잇 채널입니다";
        ChannelType channelType = ChannelType.PUBLIC;
        UUID fakeId = UUID.randomUUID();
        Channel channel = new Channel(name, description);
        channel.setType(channelType);
        ReflectionTestUtils.setField(channel, "id", fakeId);

        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest();
        request.setNewName("스프린트");
        request.setNewDescription("스프린트 채널입니다");

        ChannelDto expectDto = new ChannelDto(
                channel.getId(),
                // 이름 변경
                request.getNewName(),
                // 설명 변경
                request.getNewDescription(),
                null,
                Instant.now(),
                channel.getType()
        );
        given(channelRepository.findById(fakeId)).willReturn(Optional.of(channel));
        given(channelMapper.toDto(any(Channel.class))).willReturn(expectDto);
        // when
        ChannelDto resultDto = channelService.update(fakeId, request);
        // then
        assertEquals(expectDto.getId(), resultDto.getId());
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getType()).isEqualTo(expectDto.getType());
        assertThat(resultDto.getDescription()).isEqualTo(expectDto.getDescription());
        assertThat(resultDto.getName()).isEqualTo(expectDto.getName());
    }

    @Test
    @DisplayName("공용 채널 수정시 수정할 채널 id를 찾을 수 없어 channel not found 예외 발생히야한다")
    void update_public_channel_user_not_found_exception_fail(){
        // given
        String name = "코드잇";
        String description = "코드잇 채널입니다";
        ChannelType channelType = ChannelType.PUBLIC;
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel(name, description);
        channel.setType(channelType);
        ReflectionTestUtils.setField(channel, "id", channelId);
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest();

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(ChannelNotFoundException.class);
        then(channelMapper).should(never()).toDto(channel);
    }

    @Test
    @DisplayName("개인 채널 수정시 private channel exception 예외 발생해야 한다")
    void update_private_channel_fail(){
        // given
        ChannelType channelType = ChannelType.PRIVATE;
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel();
        channel.setType(channelType);
        ReflectionTestUtils.setField(channel, "id", channelId);
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest();

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        // when
        // then
        assertThrows(PrivateChannelUpdateException.class,
                () -> channelService.update(channelId, request));
        assertThatThrownBy(() -> channelService.update(channelId, request))
                .isInstanceOf(PrivateChannelUpdateException.class);
        then(channelMapper).should(never()).toDto(any(Channel.class));
    }

    // delete
    @Test
    @DisplayName("채널 삭제시 채널이 가지고 있는 메시지까지 삭제하는 메서드와 채널 삭제 메서드가 한번씩 실행되어야함")
    void delete_channel_success() {
        // given
        UUID channelId = UUID.randomUUID();
        Channel channel = new Channel();
        ReflectionTestUtils.setField(channel, "id", channelId);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(messageRepository.findAllByChannel(channel)).willReturn(anyList());
        // when
        channelService.delete(channelId);
        // then
        then(messageRepository).should(times(1)).deleteAll(anyList());
        then(channelRepository).should(times(1)).delete(channel);
    }
}